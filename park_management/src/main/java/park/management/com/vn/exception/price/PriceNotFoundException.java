package park.management.com.vn.exception.price;

import park.management.com.vn.exception.ErrorCode;

public class PriceNotFoundException extends PriceException {
    public PriceNotFoundException(Long priceId) {
        super(ErrorCode.PRICE_NOT_FOUND, "Price not found: id=" + priceId);
    }
}
