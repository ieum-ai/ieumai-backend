package ai.ieum.ieumai_backend.controller;

import ai.ieum.ieumai_backend.dto.ScriptApiRequest;
import ai.ieum.ieumai_backend.dto.ScriptApiResponse;
import ai.ieum.ieumai_backend.domain.Script;
import ai.ieum.ieumai_backend.service.ScriptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gpt")
@RequiredArgsConstructor
@Slf4j
public class ScriptController {
    private final ScriptService scriptService;

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

    // 활성화된 스크립트 목록 조회
    @GetMapping("/scripts")
    public ResponseEntity<List<Script>> getActiveScripts() {
        try {
            List<Script> scripts = scriptService.getActiveScripts();
            return ResponseEntity.ok(scripts);
        } catch (Exception e) {
            log.error("스크립트 조회 실패: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 스크립트 카운트 증가
    @PostMapping("/scripts/{scriptId}/count")
    public ResponseEntity<Void> incrementScriptCount(@PathVariable Long scriptId) {
        try {
            scriptService.incrementScriptCount(scriptId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("스크립트 카운트 증가 실패: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}