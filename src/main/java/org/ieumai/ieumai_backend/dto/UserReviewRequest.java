package org.ieumai.ieumai_backend.dto;

import org.ieumai.ieumai_backend.domain.enums.State;
import org.ieumai.ieumai_backend.domain.enums.City;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "사용자 리뷰 요청")
public class UserReviewRequest {
    @NotNull(message = "테스트 음성 ID는 필수 입력값입니다.")
    @Schema(description = "테스트 음성 ID")
    private Long testVoiceId;

    @NotNull(message = "지역(도)는 필수 선택값입니다.")
    @Schema(description = "지역(도)")
    private State state;

    @NotNull(message = "도시는 필수 선택값입니다.")
    @Schema(description = "도시")
    private City city;
}
