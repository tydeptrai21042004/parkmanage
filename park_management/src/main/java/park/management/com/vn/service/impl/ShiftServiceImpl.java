package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.management.com.vn.entity.Shift;
import park.management.com.vn.mapper.ShiftMapper;
import park.management.com.vn.model.request.ShiftRequest;
import park.management.com.vn.model.response.ShiftResponse;
import park.management.com.vn.repository.ShiftRepository;

import java.util.List;
import java.util.stream.Collectors;
import park.management.com.vn.service.ShiftService;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final ShiftMapper mapper;

    @Override
    public ShiftResponse createShift(ShiftRequest request) {
        Shift shift = mapper.toEntity(request);
        Shift saved = shiftRepository.save(shift);
        return mapper.toResponse(saved);
    }

    @Override
    public List<ShiftResponse> getAllShift() {
        return shiftRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ShiftResponse getShiftById(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        return mapper.toResponse(shift);
    }

    @Override
    public ShiftResponse updateShift(Long id, ShiftRequest request) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setDescription(request.getDescription());

        Shift updated = shiftRepository.save(shift);
        return mapper.toResponse(updated);
    }

    @Override
    public void deleteShift(Long id) {
        shiftRepository.deleteById(id);
    }
}
