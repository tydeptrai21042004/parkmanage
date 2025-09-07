package park.management.com.vn.service;

import park.management.com.vn.model.request.BranchStaffRequest;
import park.management.com.vn.model.response.BranchStaffResponse;

import java.util.List;

public interface BranchStaffService {

    List<BranchStaffResponse> getAllBranchStaff();

    BranchStaffResponse createBranchStaff(BranchStaffRequest request);

    BranchStaffResponse getBranchStaffById(Long id);

    void deleteBranchStaff(Long id);
}
