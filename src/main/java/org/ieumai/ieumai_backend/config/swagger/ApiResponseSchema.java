package org.ieumai.ieumai_backend.config.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "API 응답 스키마")
public class ApiResponseSchema<T> {
    @Schema(description = "응답 메시지")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;
}
