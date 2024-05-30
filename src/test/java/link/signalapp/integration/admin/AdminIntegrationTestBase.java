package link.signalapp.integration.admin;

import link.signalapp.dto.request.UserFilterDto;
import link.signalapp.dto.response.ResponseWithTotalCounts;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.integration.IntegrationTestBase;
import link.signalapp.model.Role;

public class AdminIntegrationTestBase extends IntegrationTestBase {

    protected static final String FILTER_USERS_URL = "/api/admin/users/filter";

    protected void giveAdminRoleToUser(String email) {
        giveRoleToUser(email, getAdminRole());
    }

    protected Role getAdminRole() {
        return getRoleByName(Role.ADMIN);
    }

    protected UserFilterDto createUserFilterDto() {
        return new UserFilterDto().setPage(0).setSize(10);
    }

    protected static final class UsersPage extends ResponseWithTotalCounts<UserDtoResponse> {
    }
}
