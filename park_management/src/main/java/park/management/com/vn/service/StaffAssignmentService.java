package park.management.com.vn.service;

import park.management.com.vn.model.request.StaffAssignmentRequest;
import park.management.com.vn.model.response.StaffAssignmentResponse;

import java.util.List;

public interface StaffAssignmentService {

    StaffAssignmentResponse getStaffAssignmentById(Long id);

    List<StaffAssignmentResponse> getAllStaffAssignment();

    StaffAssignmentResponse createStaffAssignment(StaffAssignmentRequest request);

    StaffAssignmentResponse updateStaffAssignment(Long id, StaffAssignmentRequest request);

    void deleteStaffAssignmentById(Long id);

    // NEW: used by controller endpoints /of-user and /of-branch
    List<StaffAssignmentResponse> getAllOfUserInMonth(Long userId, int month, int year);

    List<StaffAssignmentResponse> getAllOfBranchInMonth(Long branchId, int month, int year);
}
