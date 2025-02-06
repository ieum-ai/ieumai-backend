package ai.ieum.ieumai_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @PostMapping("/auth/login")
    @Tag(name = "인증")
    @Operation(summary = "사용자 로그인", description = "username과 password를 이용하여 로그인하고 JWT 토큰을 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(example = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Invalid username or password\"}")))
            })
    public String login(@RequestBody ai.ieum.ieumai_backend.dto.LoginRequest loginRequest) {
        return "로그인 API";
    }

    @PostMapping(value = "/voices/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "음성")
    @Operation(summary = "음성 데이터 업로드", description = "사용자의 음성 파일을 업로드하여 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "음성 파일 업로드 성공",
                            content = @Content(schema = @Schema(example = "{\"voiceId\": 456, \"message\": \"음성 파일 업로드 성공\"}")))
            })
    public String uploadVoice(@RequestParam("file") MultipartFile file) {
        return "음성 업로드 API";
    }

    @PostMapping(value = "/dialect/detect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "음성")
    @Operation(summary = "음성을 통한 방언 감지", description = "사용자가 업로드한 음성을 분석하여 방언을 감지합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "방언 감지 성공",
                            content = @Content(schema = @Schema(example = "{\"dialect\": \"경상도 방언\", \"confidence\": 0.92}")))
            })
    public String detectDialect(@RequestParam("file") MultipartFile file) {
        return "방언 감지 API";
    }
}