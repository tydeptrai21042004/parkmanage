package park.management.com.vn.exception.price;

import park.management.com.vn.exception.AppException;
import park.management.com.vn.exception.ErrorCode;

public class PriceException extends AppException {
    public PriceException(ErrorCode error) { super(error); }
    public PriceException(ErrorCode error, String message) { super(error, message); }
}
