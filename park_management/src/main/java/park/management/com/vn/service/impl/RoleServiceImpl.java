package park.management.com.vn.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.management.com.vn.entity.Role;
import park.management.com.vn.mapper.RoleMapper;
import park.management.com.vn.model.request.RoleRequest;
import park.management.com.vn.model.response.RoleResponse;
import park.management.com.vn.repository.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import park.management.com.vn.service.RoleService;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public List<RoleResponse> getAllRole() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse createRole(RoleRequest request) {
        Role entity = roleMapper.toEntity(request);
        Role saved = roleRepository.save(entity);
        return roleMapper.toResponse(saved);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role updated = roleRepository.findById(id)
                .map(role -> {
                    role.setName(request.getName());
                    role.setDescription(request.getDescription());
                    return roleRepository.save(role);
                })
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
        return roleMapper.toResponse(updated);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public List<Role> findByIdIn(List<Long> roleIds) {
        return roleRepository.findByIdIn(roleIds);
    }
}
