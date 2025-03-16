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
@Schema(description = "스크립트에 기여된 음성 파일 정보 DTO")
public class ScriptVoice {
    @Schema(description = "음성 파일 ID")
    private Long voiceFileId;

    @Schema(description = "음성 파일 생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "기여자 이름")
    private String contributorName;

    @Schema(description = "음성 길이 (밀리초)")
    private Long voiceLength;
}
