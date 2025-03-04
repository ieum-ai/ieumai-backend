package org.ieumai.ieumai_backend.dto;

import org.ieumai.ieumai_backend.domain.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "기여자 정보 수정 요청")
public class ContributorUpdateRequest {
    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Schema(description = "이름")
    private String name;

    @NotNull(message = "성별은 필수 선택값입니다.")
    @Schema(description = "성별")
    private String gender;

    @NotNull(message = "출생년도는 필수 입력값입니다.")
    @Schema(description = "출생년도")
    private Integer birthyear;

    @NotNull(message = "지역(도)는 필수 선택값입니다.")
    @Schema(description = "지역(도)")
    private State state;

    @NotNull(message = "도시는 필수 선택값입니다.")
    @Schema(description = "도시")
    private String city;
}