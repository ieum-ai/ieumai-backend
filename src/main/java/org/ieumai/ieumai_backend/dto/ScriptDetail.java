package org.ieumai.ieumai_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "스크립트 상세 정보 DTO")
public class ScriptDetail {
    @Schema(description = "스크립트 ID")
    private Long scriptId;

    @Schema(description = "스크립트 생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "스크립트 내용")
    private String script;

    @Schema(description = "기여 횟수")
    private Integer contributionCount;

    @Schema(description = "평균 음성 길이 (밀리초)")
    private double averageLength;

    @Schema(description = "마지막 업데이트로부터 경과 시간 (시간)")
    private long hoursFromLastUpdate;

    @Schema(description = "기여된 음성 파일 목록")
    private List<ScriptVoice> voiceFiles;
}