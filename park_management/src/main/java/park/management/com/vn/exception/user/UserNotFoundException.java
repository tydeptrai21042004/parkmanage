package park.management.com.vn.exception.user;

import park.management.com.vn.exception.ErrorCode;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(Long userId) {
        super(ErrorCode.USER_NOT_FOUND, "User not found: id=" + userId);
    }
}
