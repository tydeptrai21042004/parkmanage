package park.management.com.vn.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode, String message) {
        super(message != null ? message : (errorCode != null ? errorCode.getCode() : null));
        this.errorCode = errorCode;
    }

    public BaseException(String message) {
        super(message);
        this.errorCode = null; // no enum provided
    }

    public BaseException(ErrorCode errorCode) {
        super(errorCode != null ? errorCode.getCode() : null);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return errorCode != null ? errorCode.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
