package org.ieumai.ieumai_backend.controller;

import org.ieumai.ieumai_backend.dto.CommonResponse;
import org.ieumai.ieumai_backend.dto.RegionResponse;
import org.ieumai.ieumai_backend.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {
    private final RegionService regionService;

    @GetMapping
    @Operation(summary = "모든 지역 정보 조회",
            description = "모든 도/시와 각 도에 속한 시 목록을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "지역 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = RegionResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<RegionResponse> getAllRegions() {
        try {
            RegionResponse response = regionService.getAllRegions();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("지역 정보 조회 실패: " + e.getMessage());
        }
    }

    // 모든 State 목록 조회 (이전 버전 호환성을 위해 유지)
    @GetMapping("/states")
    @Operation(summary = "모든 도/시 목록 조회",
            description = "모든 도/시 목록을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "도/시 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<List<String>> getAllStates() {
        return ResponseEntity.ok(regionService.getAllStates());
    }

    // 특정 State에 속한 City 목록 조회 (이전 버전 호환성을 위해 유지)
    @GetMapping("/cities/{state}")
    @Operation(summary = "특정 도/시에 속한 시 목록 조회",
            description = "특정 도/시에 속한 모든 시 목록을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "시 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<List<String>> getCitiesByState(@PathVariable String state) {
        return ResponseEntity.ok(regionService.getCitiesByState(state));
    }
}