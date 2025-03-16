package org.ieumai.ieumai_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "음성 파일 정보 DTO")
public class VoiceFileDTO {
    @Schema(description = "음성 파일 ID")
    private Long voiceFileId;

    @Schema(description = "스크립트 내용")
    private String scriptContent;

    @Schema(description = "음성 길이 (밀리초)")
    private Long voiceLength;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
}
