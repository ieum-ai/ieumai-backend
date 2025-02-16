package ai.ieum.ieumai_backend.service;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;

import ai.ieum.ieumai_backend.domain.ContributionScript;
import ai.ieum.ieumai_backend.domain.Contributor;
import ai.ieum.ieumai_backend.domain.VoiceFile;
import ai.ieum.ieumai_backend.exception.FileStorageException;
import ai.ieum.ieumai_backend.repository.ContributionScriptRepository;
import ai.ieum.ieumai_backend.repository.ContributorRepository;
import ai.ieum.ieumai_backend.repository.VoiceFileRepository;
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
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@NoArgsConstructor(force = true)
public class VoiceService {

    private final VoiceFileRepository voiceFileRepository;
    private final ContributorRepository contributorRepository;
    private final ContributionScriptRepository contributionScriptRepository;
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public VoiceFile saveVoiceRecord(MultipartFile file, Long userId, Long scriptId, Source source) {
        Contributor contributor = contributorRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ContributionScript contributionScript = contributionScriptRepository.findById(scriptId)
                .orElseThrow(() -> new RuntimeException("Script not found"));

        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = String.format("%s/%d_%d.wav", today, userId, System.currentTimeMillis());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            VoiceFile voice = VoiceFile.builder()
                    .contributor(contributor)
                    .contributionScript(contributionScript)
                    .voiceLength(calculateVoiceLength(file))
                    .path(fileName)
                    .source(source)
                    .build();

            return voiceFileRepository.save(voice);
        } catch (IOException e) {
            log.error("Failed to save voice file to S3: ", e);
            throw new FileStorageException("음성 파일 저장에 실패했습니다.");
        }
    }

    private Double calculateVoiceLength(MultipartFile file) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file.getInputStream());
            AudioFormat format = audioStream.getFormat();
            long frames = audioStream.getFrameLength();
            return (frames + 0.0) / format.getFrameRate();
        } catch (Exception e) {
            log.error("Failed to calculate audio length: ", e);
            return 0.0;
        }
    }
}
