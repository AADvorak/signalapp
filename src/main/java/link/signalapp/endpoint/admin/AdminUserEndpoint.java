package link.signalapp.endpoint.admin;

import link.signalapp.dto.request.UserFilterDto;
import link.signalapp.dto.response.ResponseWithTotalCounts;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserEndpoint {

    private final AdminUserService adminUserService;

    @PostMapping("/filter")
    public ResponseWithTotalCounts<UserDtoResponse> filterUsers(@RequestBody UserFilterDto userFilterDto) {
        return adminUserService.filter(userFilterDto);
    }

    @PutMapping("/{userId}/roles/{roleId}")
    public void setRole(@PathVariable int userId, @PathVariable int roleId) {
        adminUserService.setRole(userId, roleId);
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public void deleteRole(@PathVariable int userId, @PathVariable int roleId) {
        adminUserService.deleteRole(userId, roleId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        adminUserService.deleteUser(userId);
    }
}
