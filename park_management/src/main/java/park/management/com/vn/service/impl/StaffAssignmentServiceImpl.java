package park.management.com.vn.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.management.com.vn.entity.BranchStaff;
import park.management.com.vn.entity.Shift;
import park.management.com.vn.entity.StaffAssignment;
import park.management.com.vn.mapper.StaffAssignmentMapper;
import park.management.com.vn.model.request.StaffAssignmentRequest;
import park.management.com.vn.model.response.StaffAssignmentResponse;
import park.management.com.vn.repository.BranchStaffRepository;
import park.management.com.vn.repository.ShiftRepository;
import park.management.com.vn.repository.StaffAssignmentRepository;
import park.management.com.vn.service.StaffAssignmentService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffAssignmentServiceImpl implements StaffAssignmentService {

    private final StaffAssignmentRepository staffAssignmentRepository;
    private final BranchStaffRepository branchStaffRepository;
    private final ShiftRepository shiftRepository;
    private final StaffAssignmentMapper mapper;

    @Override
    public StaffAssignmentResponse createStaffAssignment(StaffAssignmentRequest request) {
        BranchStaff staff = branchStaffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new EntityNotFoundException("BranchStaff with ID not found: " + request.getStaffId()));

        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new EntityNotFoundException("Shift with ID not found: " + request.getShiftId()));

        StaffAssignment entity = new StaffAssignment();
        entity.setAssignedDate(request.getAssignedDate());
        entity.setStaff(staff);
        entity.setShift(shift);

        return mapper.toResponse(staffAssignmentRepository.save(entity));
    }

    @Override
    public StaffAssignmentResponse getStaffAssignmentById(Long id) {
        StaffAssignment entity = staffAssignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StaffAssignment with ID not found: " + id));
        return mapper.toResponse(entity);
    }

    @Override
    public List<StaffAssignmentResponse> getAllStaffAssignment() {
        return staffAssignmentRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public StaffAssignmentResponse updateStaffAssignment(Long id, StaffAssignmentRequest request) {
        StaffAssignment existing = staffAssignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StaffAssignment with ID not found: " + id));

        BranchStaff staff = branchStaffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new EntityNotFoundException("BranchStaff with ID not found: " + request.getStaffId()));

        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new EntityNotFoundException("Shift with ID not found: " + request.getShiftId()));

        existing.setAssignedDate(request.getAssignedDate());
        existing.setStaff(staff);
        existing.setShift(shift);

        return mapper.toResponse(staffAssignmentRepository.save(existing));
    }

    @Override
    public void deleteStaffAssignmentById(Long id) {
        if (!staffAssignmentRepository.existsById(id)) {
            throw new EntityNotFoundException("StaffAssignment with ID not found: " + id);
        }
        staffAssignmentRepository.deleteById(id);
    }

    @Override
    public List<StaffAssignmentResponse> getAllOfUserInMonth(Long userId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        var list = staffAssignmentRepository.findAllOfUserInRange(userId, start, end);
        return list.stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<StaffAssignmentResponse> getAllOfBranchInMonth(Long branchId, int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        var list = staffAssignmentRepository.findAllOfBranchInRange(branchId, start, end);
        return list.stream().map(mapper::toResponse).toList();
    }
}
