package park.management.com.vn.exception.parkbranch;

import park.management.com.vn.exception.AppException;
import park.management.com.vn.exception.ErrorCode;

public class ParkBranchException extends AppException {
    public ParkBranchException(ErrorCode error) { super(error); }
    public ParkBranchException(ErrorCode error, String message) { super(error, message); }
}
