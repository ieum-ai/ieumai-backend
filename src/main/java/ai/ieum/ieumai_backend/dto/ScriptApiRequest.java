package ai.ieum.ieumai_backend.dto;

import lombok.Data;

@Data
public class ScriptApiRequest {
    private String prompt;                    // 사용자 입력 프롬프트
    private Integer maxTokens = 900;          // 최대 토큰 수
    private Double temperature = 0.7;         // 응답 다양성 조절
}
