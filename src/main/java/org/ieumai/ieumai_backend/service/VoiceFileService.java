package org.ieumai.ieumai_backend.service;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;

import org.ieumai.ieumai_backend.domain.VoiceFile;
import org.ieumai.ieumai_backend.domain.enums.Source;
import org.ieumai.ieumai_backend.dto.VoiceUploadResponse;
import org.ieumai.ieumai_backend.exception.FileStorageException;
import org.ieumai.ieumai_backend.repository.ContributionScriptRepository;
import org.ieumai.ieumai_backend.repository.ContributorRepository;
import org.ieumai.ieumai_backend.repository.VoiceFileRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(
        name = "cloud.aws.s3.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class VoiceFileService {
    private final VoiceFileRepository voiceFileRepository;
    private final ContributorRepository contributorRepository;
    private final ContributionScriptRepository contributionScriptRepository;
    private final Optional<AmazonS3> s3Client;
    private final String bucket;

    public VoiceFileService(
            VoiceFileRepository voiceFileRepository,
            ContributorRepository contributorRepository,
            ContributionScriptRepository contributionScriptRepository,
            Optional<AmazonS3> s3Client,
            @Value("${cloud.aws.s3.bucket:ieumai-s3-bucket}") String bucket
    ) {
        this.voiceFileRepository = voiceFileRepository;
        this.contributorRepository = contributorRepository;
        this.contributionScriptRepository = contributionScriptRepository;
        this.s3Client = s3Client;
        this.bucket = bucket;

        log.info("VoiceFileService 초기화");
        log.info("S3 버킷 이름: {}", bucket);
        log.info("S3 클라이언트 존재 여부: {}", s3Client.isPresent());

        // 버킷 존재 여부 안전하게 로깅
        s3Client.ifPresent(client -> {
            try {
                boolean bucketExists = client.doesBucketExistV2(bucket);
                log.info("S3 버킷 {} 존재 여부: {}", bucket, bucketExists);
            } catch (Exception e) {
                log.error("S3 버킷 {} 확인 중 오류 발생", bucket, e);
            }
        });
    }

    @Transactional
    public VoiceUploadResponse saveVoiceRecord(MultipartFile file, Long scriptId, Source source) {
        if (s3Client.isEmpty()) {
            log.warn("S3 클라이언트를 사용할 수 없습니다. 음성 파일 저장 취소");
            throw new FileStorageException("S3 서비스를 사용할 수 없습니다.");
        }

        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = String.format("%s/%d_%d.wav", today, scriptId, System.currentTimeMillis());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.get().putObject(bucket, fileName, file.getInputStream(), metadata);

            Long voiceLength = calculateVoiceLength(file);
            LocalDateTime now = LocalDateTime.now();

            VoiceFile voice = VoiceFile.builder()
                    .voiceLength(voiceLength)
                    .path(fileName)
                    .source(source)
                    .createdAt(now)
                    .build();

            VoiceFile savedVoice = voiceFileRepository.save(voice);

            return new VoiceUploadResponse(
                    savedVoice.getVoiceFileId(),
                    savedVoice.getPath(),
                    savedVoice.getVoiceLength(),
                    savedVoice.getSource(),
                    savedVoice.getCreatedAt()
            );
        } catch (IOException e) {
            log.error("음성 파일 S3 저장 실패", e);
            throw new FileStorageException("음성 파일 저장에 실패했습니다: " + e.getMessage());
        }
    }

    private Long calculateVoiceLength(MultipartFile file) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file.getInputStream());
            AudioFormat format = audioStream.getFormat();
            long frames = audioStream.getFrameLength();
            return Math.round((frames * 1000.0) / format.getFrameRate());
        } catch (Exception e) {
            log.error("음성 길이 계산 실패", e);
            return 0L;
        }
    }

    /**
     * 공개적으로 접근 가능한 음성 파일 리소스를 가져옵니다.
     * 모든 사용자가 접근할 수 있으며 인증이 필요하지 않습니다.
     *
     * @param voiceFileId 음성 파일 ID
     * @return 음성 파일 리소스
     */
    @Transactional(readOnly = true)
    public Resource getVoiceFileResourcePublic(Long voiceFileId) {
        // 음성 파일 조회
        VoiceFile voiceFile = voiceFileRepository.findById(voiceFileId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 음성 파일을 찾을 수 없습니다: " + voiceFileId));

        if (s3Client.isEmpty()) {
            throw new IllegalStateException("S3 클라이언트를 사용할 수 없습니다.");
        }

        // S3에서 파일 가져오기
        S3Object s3Object = s3Client.get().getObject(bucket, voiceFile.getPath());
        return new InputStreamResource(s3Object.getObjectContent());
    }
}