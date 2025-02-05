package ai.ieum.ieumai_backend.dto;

import lombok.Data;

@Data
public class ScriptApiResponse {
    private String content;                   // 생성된 스크립트 내용
    private String error;                     // 오류 메시지

    // 성공 응답 생성
    public static ScriptApiResponse success(String content) {
        ScriptApiResponse response = new ScriptApiResponse();
        response.setContent(content);
        return response;
    }

    // 오류 응답 생성
    public static ScriptApiResponse error(String error) {
        ScriptApiResponse response = new ScriptApiResponse();
        response.setError(error);
        return response;
    }
}
