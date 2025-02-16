package ai.ieum.ieumai_backend.controller;

import ai.ieum.ieumai_backend.domain.ContributionScript;
import ai.ieum.ieumai_backend.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {
    private final RegionService regionService;

    // 모든 State 목록 조회
    @GetMapping("/states")
    public ResponseEntity<List<String>> getAllStates() {
        return ResponseEntity.ok(regionService.getAllStates());
    }

    // 특정 State에 속한 City 목록 조회
    @GetMapping("/cities/{state}")
    public ResponseEntity<List<String>> getCitiesByState(@PathVariable String state) {
        return ResponseEntity.ok(regionService.getCitiesByState(state));
    }

}