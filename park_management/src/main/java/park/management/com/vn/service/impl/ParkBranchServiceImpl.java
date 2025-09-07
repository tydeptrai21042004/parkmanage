package park.management.com.vn.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import park.management.com.vn.entity.ParkBranch;
import park.management.com.vn.exception.parkbranch.ParkBranchNotFoundException;
import park.management.com.vn.mapper.ParkBranchMapper;
import park.management.com.vn.model.request.ParkBranchRequest;
import park.management.com.vn.model.response.ParkBranchResponse;
import park.management.com.vn.repository.BranchPromotionRepository;
import park.management.com.vn.repository.ParkBranchRepository;
import org.springframework.stereotype.Service;
import park.management.com.vn.service.ParkBranchService;

@Service
@RequiredArgsConstructor
public class ParkBranchServiceImpl implements ParkBranchService {

    private final BranchPromotionRepository branchPromotionRepository;
    private final ParkBranchRepository parkBranchRepository;
    private final ParkBranchMapper mapper;


    @Override
    public Optional<ParkBranch> findById(Long id) {
        return parkBranchRepository.findById(id);
    }

    @Override
    public ParkBranch getById(Long id) {
        return parkBranchRepository.findById(id)
                .orElseThrow(() -> new ParkBranchNotFoundException(id));
    }

    @Override
    public List<ParkBranchResponse> getAllBranchPark() {
        return parkBranchRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ParkBranchResponse createBranchPark(ParkBranchRequest request) {
        ParkBranch entity = mapper.toEntity(request);
        ParkBranch saved = parkBranchRepository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public ParkBranchResponse updateBranchPark(Long id, ParkBranchRequest request) {
        ParkBranch updated = parkBranchRepository.findById(id)
                .map(branch -> {
                    branch.setName(request.getName());
                    branch.setAddress(request.getAddress());
                    branch.setLocation(request.getLocation());
                    branch.setOpen(request.getOpen());
                    branch.setClose(request.getClose());
                    return parkBranchRepository.save(branch);
                })
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
        return mapper.toResponse(updated);
    }

    @Override
    public void deleteBranchPark(Long id) {
        parkBranchRepository.deleteById(id);
    }



}
