package park.management.com.vn.exception.promotion;

import park.management.com.vn.exception.ErrorCode;

public class InvalidPromotionBranchException extends PromotionException {
    public InvalidPromotionBranchException(Long promotionId) {
        super(ErrorCode.PROMOTION_INVALID_BRANCH,
              "Promotion " + promotionId + " does not belong to this branch");
    }
}
