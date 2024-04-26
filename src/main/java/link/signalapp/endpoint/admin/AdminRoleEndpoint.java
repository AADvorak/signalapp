package link.signalapp.endpoint.admin;

import link.signalapp.dto.response.RoleDtoResponse;
import link.signalapp.service.admin.AdminRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleEndpoint {

    private final AdminRoleService adminRoleService;

    @GetMapping
    public List<RoleDtoResponse> getAll() {
        return adminRoleService.getAll();
    }
}
