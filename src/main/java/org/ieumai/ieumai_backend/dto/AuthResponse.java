package org.ieumai.ieumai_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "인증 응답")
public class AuthResponse {
    @Schema(description = "인증 토큰")
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
