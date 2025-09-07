package park.management.com.vn.exception.promotion;

import park.management.com.vn.exception.AppException;
import park.management.com.vn.exception.ErrorCode;

public class PromotionException extends AppException {
    public PromotionException(ErrorCode error) { super(error); }
    public PromotionException(ErrorCode error, String message) { super(error, message); }
}
