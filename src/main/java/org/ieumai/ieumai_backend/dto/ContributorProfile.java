package org.ieumai.ieumai_backend.dto;

import org.ieumai.ieumai_backend.domain.enums.State;
import org.ieumai.ieumai_backend.domain.enums.City;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "기여자 프로필")
public class ContributorProfile {
    @Schema(description = "기여자 ID")
    private Long contributorId;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "성별")
    private String gender;

    @Schema(description = "출생년도")
    private Integer birthyear;

    @Schema(description = "지역(도)")
    private State state;

    @Schema(description = "도시")
    private City city;

    @Schema(description = "총 기여 횟수")
    private Integer totalContributions;
}