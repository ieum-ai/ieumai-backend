package org.ieumai.ieumai_backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.ieumai.ieumai_backend.domain.Contribution;
import org.ieumai.ieumai_backend.domain.ContributionScriptVoiceFile;
import org.ieumai.ieumai_backend.domain.Contributor;
import org.ieumai.ieumai_backend.domain.VoiceFile;
import org.ieumai.ieumai_backend.dto.VoiceFileDTO;
import org.ieumai.ieumai_backend.repository.ContributionRepository;
import org.ieumai.ieumai_backend.repository.ContributionScriptVoiceFileRepository;
import org.ieumai.ieumai_backend.repository.ContributorRepository;
import org.ieumai.ieumai_backend.repository.VoiceFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContributionService {
    private final ContributionRepository contributionRepository;
    private final ContributionScriptVoiceFileRepository voiceFileRepository;
    private final ContributorRepository contributorRepository;
    private final VoiceFileRepository fileRepository;
    private final Optional<AmazonS3> s3Client;

    @Value("${cloud.aws.s3.bucket:ieumai-s3-bucket}")
    private String bucket;

    /**
     * 특정 사용자의 최근 기여 음성 파일 목록을 조회합니다.
     * @param email 사용자 이메일
     * @param limit 조회할 최대 개수
     * @return 최근 기여 음성 파일 목록
     */
    @Transactional(readOnly = true)
    public List<VoiceFileDTO> getRecentContributions(String email, int limit) {
        Contributor contributor = contributorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 기여자를 찾을 수 없습니다: " + email));

        List<Contribution> contributions = contributionRepository.findByContributor_ContributorId(contributor.getContributorId());

        if (contributions.isEmpty()) {
            return new ArrayList<>();
        }

        List<VoiceFileDTO> result = new ArrayList<>();

        for (Contribution contribution : contributions) {
            List<ContributionScriptVoiceFile> voiceFiles = voiceFileRepository
                    .findByContribution_ContributionId(contribution.getContributionId());

            for (ContributionScriptVoiceFile voiceFile : voiceFiles) {
                result.add(VoiceFileDTO.builder()
                        .voiceFileId(voiceFile.getVoiceFile().getVoiceFileId())
                        .scriptContent(voiceFile.getContributionScript().getContributionScript())
                        .voiceLength(voiceFile.getVoiceFile().getVoiceLength())
                        .createdAt(voiceFile.getCreatedAt())
                        .build());

                if (result.size() >= limit) {
                    return result;
                }
            }
        }

        return result;
    }

    /**
     * 특정 음성 파일의 리소스를 가져옵니다.
     * @param voiceFileId 음성 파일 ID
     * @param email 요청자 이메일
     * @return 음성 파일 리소스
     */
    @Transactional(readOnly = true)
    public Resource getVoiceFileResource(Long voiceFileId, String email) {
        // 사용자 존재 확인
        Contributor contributor = contributorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 기여자를 찾을 수 없습니다: " + email));

        // 음성 파일 조회
        VoiceFile voiceFile = fileRepository.findById(voiceFileId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 음성 파일을 찾을 수 없습니다: " + voiceFileId));

        if (s3Client.isEmpty()) {
            throw new IllegalStateException("S3 클라이언트를 사용할 수 없습니다.");
        }

        // S3에서 파일 가져오기
        S3Object s3Object = s3Client.get().getObject(bucket, voiceFile.getPath());
        return new InputStreamResource(s3Object.getObjectContent());
    }

    /**
     * 특정 음성 파일을 삭제합니다.
     * @param voiceFileId 음성 파일 ID
     * @param email 요청자 이메일
     */
    @Transactional
    public void deleteVoiceFile(Long voiceFileId, String email) {
        // 사용자 존재 확인
        Contributor contributor = contributorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 기여자를 찾을 수 없습니다: " + email));

        // 음성 파일 조회
        VoiceFile voiceFile = fileRepository.findById(voiceFileId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 음성 파일을 찾을 수 없습니다: " + voiceFileId));

        // 사용자가 해당 파일의 소유자인지 확인
        boolean isOwner = false;
        List<Contribution> contributions = contributionRepository.findByContributor_ContributorId(contributor.getContributorId());

        for (Contribution contribution : contributions) {
            List<ContributionScriptVoiceFile> voiceFiles = voiceFileRepository
                    .findByContribution_ContributionId(contribution.getContributionId());

            isOwner = voiceFiles.stream()
                    .anyMatch(vf -> vf.getVoiceFile().getVoiceFileId().equals(voiceFileId));

            if (isOwner) {
                break;
            }
        }

        if (!isOwner) {
            throw new IllegalArgumentException("해당 음성 파일을 삭제할 권한이 없습니다.");
        }

        // S3에서 파일 삭제
        if (s3Client.isPresent()) {
            s3Client.get().deleteObject(bucket, voiceFile.getPath());
        }

        // 데이터베이스에서 관련 레코드 삭제
        // 먼저 ContributionScriptVoiceFile에서 삭제
        List<ContributionScriptVoiceFile> voiceFileRecords = voiceFileRepository.findAll().stream()
                .filter(vf -> vf.getVoiceFile().getVoiceFileId().equals(voiceFileId))
                .collect(Collectors.toList());

        voiceFileRepository.deleteAll(voiceFileRecords);

        // 그 다음 VoiceFile 삭제
        fileRepository.deleteById(voiceFileId);
    }
}