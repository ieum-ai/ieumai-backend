package org.ieumai.ieumai_backend.controller;

import org.ieumai.ieumai_backend.domain.TestScript;
import org.ieumai.ieumai_backend.service.TestScriptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/test-scripts")
@RequiredArgsConstructor
public class TestScriptController {

    private final TestScriptService testScriptService;

    // 랜덤한 활성 스크립트 2개를 JSON 배열 형태로 반환
    @GetMapping("/testScript")
    public ResponseEntity<String> getRandomScripts() {
        try {
            String scripts = testScriptService.getRandomActiveScriptsAsJson();
            return ResponseEntity.ok(scripts);
        } catch (Exception e) {
            log.error("랜덤 스크립트 조회 중 오류 발생: ", e);
            return ResponseEntity.internalServerError().body("스크립트 조회 실패: " + e.getMessage());
        }
    }

    // 스크립트의 기여 횟수를 증가
    @PostMapping("/{testScriptId}/increment")
    public ResponseEntity<TestScript> incrementTestCount(
            @PathVariable Long testScriptId
    ) {
        try {
            TestScript updatedScript = testScriptService.incrementTestCount(testScriptId);
            return ResponseEntity.ok(updatedScript);
        } catch (Exception e) {
            log.error("기여 횟수 증가 중 오류 발생: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}