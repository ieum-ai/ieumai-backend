package org.ieumai.ieumai_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "테스트 보고서")
public class TestReport {
    @Schema(description = "테스트 ID")
    private Long testId;

    @Schema(description = "테스트 UUID")
    private String uuid;

    @Schema(description = "보고서 버전")
    private Integer reportVersion;

    @Schema(description = "보고서 내용 (JSON)")
    private String testReport;
}
