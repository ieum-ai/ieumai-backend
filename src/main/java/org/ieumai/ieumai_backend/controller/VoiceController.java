package org.ieumai.ieumai_backend.controller;

import org.ieumai.ieumai_backend.dto.CommonResponse;
import org.ieumai.ieumai_backend.dto.VoiceUploadRequest;
import org.ieumai.ieumai_backend.dto.VoiceUploadResponse;
import org.ieumai.ieumai_backend.service.VoiceFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/voice")
@RequiredArgsConstructor
public class VoiceController {
    private final VoiceFileService voiceFileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "음성 파일 업로드",
            description = "사용자의 음성 파일을 업로드합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "음성 파일 업로드 성공",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<CommonResponse<VoiceUploadResponse>> uploadVoice(
            @RequestParam("file") MultipartFile file,
            @Valid @ModelAttribute VoiceUploadRequest request
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new CommonResponse<>("음성 파일이 없습니다."));
        }

        try {
            VoiceUploadResponse response = voiceFileService.saveVoiceRecord(file, request.getScriptId(), request.getSource());
            return ResponseEntity.ok(new CommonResponse<>("음성이 성공적으로 저장되었습니다.", response));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new CommonResponse<>("음성 저장에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/{voiceFileId}/play")
    @Operation(summary = "음성 파일 재생",
            description = "특정 음성 파일을 스트리밍하여 재생합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "음성 파일 스트리밍 성공"),
            @ApiResponse(responseCode = "404",
                    description = "음성 파일을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<Resource> playVoiceFile(@PathVariable Long voiceFileId) {
        try {
            Resource voiceResource = voiceFileService.getVoiceFileResourcePublic(voiceFileId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/wav"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + voiceResource.getFilename() + "\"")
                    .body(voiceResource);
        } catch (Exception e) {
            log.error("음성 파일 재생 중 오류 발생: ", e);
            return ResponseEntity.notFound().build();
        }
    }
}