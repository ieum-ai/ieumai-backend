package org.ieumai.ieumai_backend.controller;

import org.ieumai.ieumai_backend.domain.ContributionScript;
import org.ieumai.ieumai_backend.service.ContributionScriptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/contribution-scripts")
@RequiredArgsConstructor
public class ContributionScriptController {

    private final ContributionScriptService contributionScriptService;

    // 랜덤한 활성 스크립트 3개를 JSON 배열 형태로 반환
    @GetMapping("/contributionScript")
    public ResponseEntity<String> getRandomScripts() {
        try {
            String scripts = contributionScriptService.getRandomActiveScriptsAsJson();
            return ResponseEntity.ok(scripts);
        } catch (Exception e) {
            log.error("랜덤 스크립트 조회 중 오류 발생: ", e);
            return ResponseEntity.internalServerError().body("스크립트 조회 실패: " + e.getMessage());
        }
    }

    // 스크립트의 기여 횟수를 증가
    @PostMapping("/{contributionScriptId}/increment")
    public ResponseEntity<ContributionScript> incrementContributionCount(
            @PathVariable Long contributionScriptId
    ) {
        try {
            ContributionScript updatedScript = contributionScriptService.incrementContributionCount(contributionScriptId);
            return ResponseEntity.ok(updatedScript);
        } catch (Exception e) {
            log.error("기여 횟수 증가 중 오류 발생: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}