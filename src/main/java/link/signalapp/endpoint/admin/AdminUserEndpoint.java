package link.signalapp.endpoint.admin;

import link.signalapp.dto.request.paging.UsersPageDtoRequest;
import link.signalapp.dto.response.PageDtoResponse;
import link.signalapp.dto.response.RoleDtoResponse;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserEndpoint {

    private final AdminUserService adminUserService;

    @PostMapping("/filter")
    public PageDtoResponse<UserDtoResponse> getPage(@RequestBody UsersPageDtoRequest request) {
        return adminUserService.getPage(request);
    }

    @PutMapping("/{userId}/roles/{roleId}")
    public List<RoleDtoResponse> setRole(@PathVariable int userId, @PathVariable int roleId) {
        return adminUserService.setRole(userId, roleId);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public List<RoleDtoResponse> deleteRole(@PathVariable int userId, @PathVariable int roleId) {
        return adminUserService.deleteRole(userId, roleId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        adminUserService.deleteUser(userId);
    }
}
