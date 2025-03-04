package org.ieumai.ieumai_backend.dto;

import org.ieumai.ieumai_backend.domain.enums.Source;
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
@Schema(description = "음성 파일 업로드 응답")
public class VoiceUploadResponse {
    @Schema(description = "음성 파일 ID")
    private Long voiceFileId;

    @Schema(description = "파일 경로")
    private String path;

    @Schema(description = "음성 길이 (밀리초)")
    private Long voiceLength;

    @Schema(description = "출처")
    private Source source;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
}
