package ai.ieum.ieumai_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse<T> {
    private String message;
    private T data;

    public CommonResponse(String message) {
        this.message = message;
    }

    public CommonResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
