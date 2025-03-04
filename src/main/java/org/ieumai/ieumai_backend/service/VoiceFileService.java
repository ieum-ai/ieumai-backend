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
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@NoArgsConstructor(force = true)
public class VoiceFileService {

    private final VoiceFileRepository voiceFileRepository;
    private final ContributorRepository contributorRepository;
    private final ContributionScriptRepository contributionScriptRepository;
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public VoiceUploadResponse saveVoiceRecord(MultipartFile file, Long scriptId, Source source) {
        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = String.format("%s/%d_%d.wav", today, scriptId, System.currentTimeMillis());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            Long voiceLength = calculateVoiceLength(file);
            LocalDateTime now = LocalDateTime.now();

            VoiceFile voice = VoiceFile.builder()
                    .voiceLength(voiceLength)
                    .path(fileName)
                    .source(source)
                    .createdAt(now)
                    .build();

            VoiceFile savedVoice = voiceFileRepository.save(voice);

            // Entity를 DTO로 변환
            return new VoiceUploadResponse(
                    savedVoice.getVoiceFileId(),
                    savedVoice.getPath(),
                    savedVoice.getVoiceLength(),
                    savedVoice.getSource(),
                    savedVoice.getCreatedAt()
            );
        } catch (IOException e) {
            log.error("Failed to save voice file to S3: ", e);
            throw new FileStorageException("음성 파일 저장에 실패했습니다: " + e.getMessage());
        }
    }

    private Long calculateVoiceLength(MultipartFile file) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file.getInputStream());
            AudioFormat format = audioStream.getFormat();
            long frames = audioStream.getFrameLength();
            // 초 단위를 밀리초 단위로 변환 (1초 = 1000밀리초)
            return Math.round((frames * 1000.0) / format.getFrameRate());
        } catch (Exception e) {
            log.error("Failed to calculate audio length: ", e);
            return 0L;
        }
    }
}