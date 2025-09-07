package park.management.com.vn.exception.user;

import park.management.com.vn.exception.AppException;
import park.management.com.vn.exception.ErrorCode;

public class UserException extends AppException {
    public UserException(ErrorCode error) { super(error); }
    public UserException(ErrorCode error, String message) { super(error, message); }
}
