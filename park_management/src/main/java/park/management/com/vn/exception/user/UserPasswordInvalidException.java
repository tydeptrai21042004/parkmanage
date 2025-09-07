package park.management.com.vn.exception.user;

import park.management.com.vn.exception.ErrorCode;

public class UserPasswordInvalidException extends UserException {
    public UserPasswordInvalidException() {
        super(ErrorCode.USER_PASSWORD_INVALID);
    }
}
