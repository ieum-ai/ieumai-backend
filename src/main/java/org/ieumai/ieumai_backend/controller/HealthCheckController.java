package org.ieumai.ieumai_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @Operation(summary = "서비스 상태 확인",
            description = "현재 API 서비스가 정상 작동하는지 확인합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "서비스 정상 작동",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is running");
    }
}