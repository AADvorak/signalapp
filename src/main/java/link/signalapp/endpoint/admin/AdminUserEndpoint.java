package link.signalapp.endpoint.admin;

import link.signalapp.dto.request.UserFilterDto;
import link.signalapp.dto.response.ResponseWithTotalCounts;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserEndpoint {

    private final AdminUserService adminUserService;

    @PostMapping("/filter")
    public ResponseWithTotalCounts<UserDtoResponse> filterUsers(@RequestBody UserFilterDto userFilterDto) {
        return adminUserService.filter(userFilterDto);
    }
}
