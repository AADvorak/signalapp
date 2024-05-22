package link.signalapp.integration.admin;

import link.signalapp.dto.request.UserFilterDto;
import link.signalapp.dto.response.ResponseWithTotalCounts;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.integration.IntegrationTestBase;
import link.signalapp.model.Role;
import link.signalapp.model.User;
import link.signalapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class AdminIntegrationTestBase extends IntegrationTestBase {

    protected static final String FILTER_USERS_URL = "/api/admin/users/filter";

    protected static final String ADMIN_ROLE_NAME = "ADMIN";

    @Autowired
    protected RoleRepository roleRepository;

    protected void giveAdminRoleToUser(String email) {
        giveRoleToUser(email, getAdminRole());
    }

    protected void giveRoleToUser(String email, Role role) {
        User user = userRepository.findByEmail(email);
        user.setRoles(Set.of(role));
        userRepository.save(user);
    }

    protected Role getAdminRole() {
        return roleRepository.findAll().stream()
                .filter(role -> ADMIN_ROLE_NAME.equals(role.getName()))
                .findAny().orElseThrow();
    }

    protected UserFilterDto createUserFilterDto() {
        return new UserFilterDto().setPage(0).setSize(10);
    }

    protected static final class UsersPage extends ResponseWithTotalCounts<UserDtoResponse> {
    }
}
