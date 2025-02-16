package ai.ieum.ieumai_backend.controller;

import ai.ieum.ieumai_backend.domain.VoiceFile;
import ai.ieum.ieumai_backend.dto.CommonResponse;
import ai.ieum.ieumai_backend.service.VoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voices")
@RequiredArgsConstructor
public class VoiceController {
    private final VoiceService voiceService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVoice(
            @RequestParam("file") MultipartFile file,
            @RequestParam("scriptId") Long scriptId,
            @RequestParam("source") Source source,
            @RequestAttribute("userId") Long userId
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new CommonResponse("음성 파일이 없습니다."));
        }

        try {
            VoiceFile savedVoice = voiceService.saveVoiceRecord(file, userId, scriptId, source);
            return ResponseEntity.ok(new CommonResponse("음성이 성공적으로 저장되었습니다.", savedVoice));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CommonResponse("음성 저장에 실패했습니다: " + e.getMessage()));
        }
    }
}
