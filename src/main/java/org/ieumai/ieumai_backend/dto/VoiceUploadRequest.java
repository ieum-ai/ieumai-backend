package org.ieumai.ieumai_backend.dto;

import org.ieumai.ieumai_backend.domain.enums.Source;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "음성 파일 업로드 요청")
public class VoiceUploadRequest {
    @NotNull(message = "스크립트 ID는 필수 입력값입니다.")
    @Schema(description = "스크립트 ID")
    private Long scriptId;

    @NotNull(message = "출처는 필수 선택값입니다.")
    @Schema(description = "출처 (Contribution 또는 Test)")
    private Source source;
}
