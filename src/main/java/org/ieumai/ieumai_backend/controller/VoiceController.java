package org.ieumai.ieumai_backend.controller;

import org.ieumai.ieumai_backend.domain.VoiceFile;
import org.ieumai.ieumai_backend.domain.enums.Source;
import org.ieumai.ieumai_backend.dto.CommonResponse;
import org.ieumai.ieumai_backend.service.VoiceFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voice")
@RequiredArgsConstructor
public class VoiceController {
    private final VoiceFileService voiceFileService;

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
            VoiceFile savedVoice = voiceFileService.saveVoiceRecord(file, userId, scriptId, source);
            return ResponseEntity.ok(new CommonResponse("음성이 성공적으로 저장되었습니다.", savedVoice));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CommonResponse("음성 저장에 실패했습니다: " + e.getMessage()));
        }
    }
}
