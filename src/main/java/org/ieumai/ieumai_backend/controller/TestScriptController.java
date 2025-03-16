package org.ieumai.ieumai_backend.controller;

import org.ieumai.ieumai_backend.domain.TestScript;
import org.ieumai.ieumai_backend.dto.CommonResponse;
import org.ieumai.ieumai_backend.dto.TestScriptResponse;
import org.ieumai.ieumai_backend.service.TestScriptService;
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
@RequestMapping("/test-scripts")
@RequiredArgsConstructor
public class TestScriptController {

    private final TestScriptService testScriptService;

    // 랜덤한 활성 스크립트 2개를 반환
    @GetMapping("/testScript")
    @Operation(summary = "랜덤 테스트 스크립트 조회",
            description = "랜덤한 활성 스크립트 2개를 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "스크립트 조회 성공",
                    content = @Content(schema = @Schema(implementation = TestScriptResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<List<TestScriptResponse>> getRandomScripts() {
        try {
            List<TestScriptResponse> scripts = testScriptService.getRandomActiveScripts();
            return ResponseEntity.ok(scripts);
        } catch (Exception e) {
            log.error("랜덤 스크립트 조회 중 오류 발생: ", e);
            throw new RuntimeException("스크립트 조회 실패: " + e.getMessage());
        }
    }

    // 스크립트의 테스트 횟수를 증가
    @PostMapping("/{testScriptId}/increment")
    @Operation(summary = "스크립트 테스트 횟수 증가",
            description = "특정 스크립트의 테스트 횟수를 증가시킵니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "테스트 횟수 증가 성공",
                    content = @Content(schema = @Schema(implementation = TestScript.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    })
    public ResponseEntity<TestScript> incrementTestCount(
            @PathVariable Long testScriptId
    ) {
        try {
            TestScript updatedScript = testScriptService.incrementTestCount(testScriptId);
            return ResponseEntity.ok(updatedScript);
        } catch (Exception e) {
            log.error("테스트 횟수 증가 중 오류 발생: ", e);
            throw new RuntimeException("테스트 횟수 증가 실패: " + e.getMessage());
        }
    }
}