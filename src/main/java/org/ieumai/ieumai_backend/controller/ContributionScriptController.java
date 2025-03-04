package org.ieumai.ieumai_backend.controller;

import org.ieumai.ieumai_backend.domain.ContributionScript;
import org.ieumai.ieumai_backend.dto.CommonResponse;
import org.ieumai.ieumai_backend.dto.ContributionScriptResponse;
import org.ieumai.ieumai_backend.service.ContributionScriptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/contribution-scripts")
@RequiredArgsConstructor
public class ContributionScriptController {

    private final ContributionScriptService contributionScriptService;

    // 랜덤한 활성 스크립트 3개를 반환
    @GetMapping("/contributionScript")
    @Operation(summary = "랜덤 기여 스크립트 조회",
            description = "랜덤한 활성 스크립트 3개를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "스크립트 조회 성공",
                    content = @Content(schema = @Schema(implementation = ContributionScriptResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<List<ContributionScriptResponse>> getRandomScripts() {
        try {
            List<ContributionScriptResponse> scripts = contributionScriptService.getRandomActiveScripts();
            return ResponseEntity.ok(scripts);
        } catch (Exception e) {
            log.error("랜덤 스크립트 조회 중 오류 발생: ", e);
            throw new RuntimeException("스크립트 조회 실패: " + e.getMessage());
        }
    }

    // 스크립트의 기여 횟수를 증가
    @PostMapping("/{contributionScriptId}/increment")
    @Operation(summary = "스크립트 기여 횟수 증가",
            description = "특정 스크립트의 기여 횟수를 증가시킵니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "기여 횟수 증가 성공",
                    content = @Content(schema = @Schema(implementation = ContributionScript.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<ContributionScript> incrementContributionCount(
            @PathVariable Long contributionScriptId
    ) {
        try {
            ContributionScript updatedScript = contributionScriptService.incrementContributionCount(contributionScriptId);
            return ResponseEntity.ok(updatedScript);
        } catch (Exception e) {
            log.error("기여 횟수 증가 중 오류 발생: ", e);
            throw new RuntimeException("기여 횟수 증가 실패: " + e.getMessage());
        }
    }
}