package park.management.com.vn.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    // When the error happened
    private LocalDateTime timestamp;

    // HTTP status code
    private int status;

    // HTTP status name (example: "Not Found")
    private String error;

    // Human-readable explanation
    private String message;

    // Internal system error code
    private String errorCode;
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

}
