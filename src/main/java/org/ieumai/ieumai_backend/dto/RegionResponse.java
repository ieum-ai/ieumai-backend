package org.ieumai.ieumai_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "지역 정보 응답")
public class RegionResponse {
    @Schema(description = "모든 도/시 목록")
    private List<String> states;

    @Schema(description = "도별 시 목록")
    private Map<String, List<String>> citiesByState;
}