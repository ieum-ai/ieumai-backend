package ai.ieum.ieumai_backend.exception;

public class IpLimitException extends RuntimeException {
    public IpLimitException(String message) {
        super(message);
    }
}
