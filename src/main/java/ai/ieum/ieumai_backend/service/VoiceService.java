package ai.ieum.ieumai_backend.service;

import ai.ieum.ieumai_backend.domain.Voice;
import ai.ieum.ieumai_backend.domain.enums.Source;
import ai.ieum.ieumai_backend.exception.FileStorageException;
import ai.ieum.ieumai_backend.repository.VoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceService {
    private final VoiceRepository voiceRepository;

    @Value("${file.upload.path}")  // application.yml에서 설정
    private String uploadPath;

    @Transactional
    public Voice saveVoiceRecord(MultipartFile file, Long userId, Long scriptId, Source source) {
        try {
            // 파일 저장 경로 생성 (yyyy/MM/dd/userId_timestamp.wav)
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = String.format("%d_%d.wav", userId, System.currentTimeMillis());
            String relativePath = today + "/" + fileName;

            // 전체 경로 생성
            Path directoryPath = Paths.get(uploadPath, today);
            Path filePath = directoryPath.resolve(fileName);

            // 디렉토리가 없으면 생성
            Files.createDirectories(directoryPath);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Voice 엔티티 생성 및 저장
            Voice voice = Voice.builder()
                    .userId(userId)
                    .scriptId(scriptId)
                    .duration(calculateDuration(file))  // 음성 파일 길이 계산
                    .path(relativePath)  // 상대 경로 저장
                    .source(source)
                    .build();

            return voiceRepository.save(voice);

        } catch (IOException e) {
            log.error("Failed to save voice file: ", e);
            throw new FileStorageException("음성 파일 저장에 실패했습니다.");
        }
    }

    private Double calculateDuration(MultipartFile file) {
        // 음성 파일 길이 계산 로직 구현
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file.getInputStream());
            AudioFormat format = audioStream.getFormat();
            long frames = audioStream.getFrameLength();
            return (frames + 0.0) / format.getFrameRate();
        } catch (Exception e) {
            log.error("Failed to calculate audio duration: ", e);
            return 0.0;
        }
    }
}
