package ai.ieum.ieumai_backend.controller;

import ai.ieum.ieumai_backend.config.swagger.ApiResponseSchema;
import ai.ieum.ieumai_backend.dto.AuthResponse;
import ai.ieum.ieumai_backend.dto.CommonResponse;
import ai.ieum.ieumai_backend.dto.EmailRequest;
import ai.ieum.ieumai_backend.dto.VerifyPinRequest;
import ai.ieum.ieumai_backend.exception.IpLimitException;
import ai.ieum.ieumai_backend.exception.PinVerificationException;
import ai.ieum.ieumai_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/request-pin")
    @Operation(summary = "PIN 코드 요청",
            description = "이메일로 PIN 코드를 전송합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "PIN 전송 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponseSchema.class))),
            @ApiResponse(responseCode = "429",
                    description = "요청 제한 초과",
                    content = @Content(schema = @Schema(implementation = ApiResponseSchema.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    })
    public ResponseEntity<?> requestPin(
            @Valid @RequestBody @Email(message = "올바른 이메일 형식이 아닙니다.") EmailRequest request,
            HttpServletRequest servletRequest) {
        try {
            authService.requestPin(request.getEmail(), getClientIp(servletRequest));
            return ResponseEntity.ok(new CommonResponse("인증번호가 이메일로 전송되었습니다."));
        } catch (IpLimitException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new CommonResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("PIN 요청 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse("서버 오류가 발생했습니다."));
        }
    }

    @PostMapping("/verify-pin")
    @Operation(summary = "PIN 코드 검증",
            description = "사용자 이메일로 전송된 PIN 코드를 검증합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "PIN 검증 성공",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "잘못된 PIN",
                    content = @Content(schema = @Schema(implementation = ApiResponseSchema.class))),
            @ApiResponse(responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    })
    public ResponseEntity<?> verifyPin(@Valid @RequestBody VerifyPinRequest request) {
        try {
            String token = authService.verifyPin(request.getEmail(), request.getPin());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (PinVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("PIN 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse("서버 오류가 발생했습니다."));
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String[] HEADERS_TO_TRY = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED"
        };

        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.contains(",") ? ip.split(",")[0].trim() : ip;
            }
        }

        return request.getRemoteAddr();
    }
}
