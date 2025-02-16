package ai.ieum.ieumai_backend.dto;

import ai.ieum.ieumai_backend.domain.enums.State;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpRequest {
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "성별은 필수 선택값입니다.")
    private String gender;

    @NotNull(message = "출생년도는 필수 입력값입니다.")
    private Integer birthyear;

    @NotNull(message = "지역(도)는 필수 선택값입니다.")
    private State state;

    private String city;
}
