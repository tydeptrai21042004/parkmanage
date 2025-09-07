package park.management.com.vn.exception;

import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {
    private final ErrorCode error;

    public AppException(ErrorCode error) {
        super(error.getMessage());
        this.error = error;
    }

    public AppException(ErrorCode error, String overrideMessage) {
        super(overrideMessage);
        this.error = error;
    }

    public ErrorCode getError() { return error; }
    public String getCode()    { return error.getCode(); }
    public HttpStatus getStatus() { return error.getStatus(); }
}
