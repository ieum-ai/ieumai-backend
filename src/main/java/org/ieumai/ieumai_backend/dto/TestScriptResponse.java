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
@Schema(description = "테스트 스크립트 응답")
public class TestScriptResponse {
    @Schema(description = "스크립트 ID")
    private Long testScriptId;

    @Schema(description = "스크립트 내용")
    private String testScript;
}
