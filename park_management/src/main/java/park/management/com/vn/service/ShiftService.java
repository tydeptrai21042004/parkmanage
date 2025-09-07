package park.management.com.vn.service;

import park.management.com.vn.model.request.ShiftRequest;
import park.management.com.vn.model.response.ShiftResponse;

import java.util.List;

public interface ShiftService {

    List<ShiftResponse> getAllShift();

    ShiftResponse createShift(ShiftRequest request);

    ShiftResponse getShiftById(Long id);

    ShiftResponse updateShift(Long id, ShiftRequest request);

    void deleteShift(Long id);
}
