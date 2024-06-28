package link.signalapp.integration.admin;

import link.signalapp.dto.request.paging.UsersPageDtoRequest;
import link.signalapp.dto.response.PageDtoResponse;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.integration.IntegrationTestBase;
import link.signalapp.model.Role;

public class AdminIntegrationTestBase extends IntegrationTestBase {

    protected static final String USERS_PAGE_URL = "/api/admin/users/page";

    protected void giveAdminRoleToUser(String email) {
        giveRoleToUser(email, getAdminRole());
    }

    protected Role getAdminRole() {
        return getRoleByName(Role.ADMIN);
    }

    protected UsersPageDtoRequest createRequest() {
        return new UsersPageDtoRequest();
    }

    protected static final class UsersPage extends PageDtoResponse<UserDtoResponse> {
    }
}
