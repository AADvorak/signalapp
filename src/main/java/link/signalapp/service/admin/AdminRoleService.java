package link.signalapp.service.admin;

import link.signalapp.dto.response.RoleDtoResponse;
import link.signalapp.mapper.RoleMapper;
import link.signalapp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoleService {

    private final RoleRepository roleRepository;

    public List<RoleDtoResponse> getAll() {
        return roleRepository.findAll().stream().map(RoleMapper.INSTANCE::roleToDto).toList();
    }
}
