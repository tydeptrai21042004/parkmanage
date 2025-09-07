package park.management.com.vn.exception.parkbranch;

import park.management.com.vn.exception.ErrorCode;

public class ParkBranchNotFoundException extends ParkBranchException {
    public ParkBranchNotFoundException(Long branchId) {
        super(ErrorCode.PARK_BRANCH_NOT_FOUND, "Park branch not found: id=" + branchId);
    }
}
