package org.ieumai.ieumai_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "PIN 검증 요청")
public class VerifyPinRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @NotBlank(message = "PIN 코드는 필수 입력값입니다.")
    @Schema(description = "PIN 코드", example = "123456")
    private String pin;
}
