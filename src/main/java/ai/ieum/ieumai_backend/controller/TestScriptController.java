package ai.ieum.ieumai_backend.controller;

import ai.ieum.ieumai_backend.domain.TestScript;
import ai.ieum.ieumai_backend.service.ContributionScriptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gpt")
@RequiredArgsConstructor
@Slf4j
public class TestScriptController {
    private final ContributionScriptService scriptService;

    // 스크립트 생성
    @PostMapping
    public ResponseEntity<ScriptApiResponse> generateCustomScript(@RequestBody ScriptApiRequest request) {
        try {
            String response = scriptService.generateResponse(request.getPrompt());
            return ResponseEntity.ok(ScriptApiResponse.success(response));
        } catch (Exception e) {
            log.error("스크립트 생성 실패: ", e);
            return ResponseEntity.internalServerError()
                    .body(ScriptApiResponse.error("스크립트 생성 실패: " + e.getMessage()));
        }
    }

    // 전체 스크립트 목록 조회
    @GetMapping("/scripts")
    public ResponseEntity<List<TestScript>> getScripts() {
        try {
            List<TestScript> TestScripts = scriptService.getScripts();
            return ResponseEntity.ok(TestScripts);
        } catch (Exception e) {
            log.error("스크립트 조회 실패: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 활성화된 스크립트 목록 조회
    @GetMapping("/scripts/active")
    public ResponseEntity<List<TestScript>> getActiveScripts() {
        try {
            List<TestScript> TestScripts = scriptService.getActiveScripts();
            return ResponseEntity.ok(TestScripts);
        } catch (Exception e) {
            log.error("스크립트 조회 실패: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 활성화된 스크립트 중 하나를 선택
    @GetMapping("/scripts/random")
    public ResponseEntity<TestScript> getOneActiveScript() {
        try {
            TestScript TestScript = scriptService.getOneActiveScript();
            return ResponseEntity.ok(TestScript);
        } catch (Exception e) {
            log.error("스크립트 조회 실패: ", e);
            return ResponseEntity.internalServerError().build();
        }

    }
