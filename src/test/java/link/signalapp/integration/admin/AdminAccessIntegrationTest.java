package link.signalapp.integration.admin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminAccessIntegrationTest extends AdminIntegrationTestBase {

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
        registerUsers();
        giveAdminRoleToUser(email1);
    }

    @Test
    public void adminAccess() {
        ResponseEntity<UsersPage> response = template.exchange(fullUrl(FILTER_USERS_URL), HttpMethod.POST,
                new HttpEntity<>(createUserFilterDto(), login(email1)), UsersPage.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void noAdminNoAccess() {
        // todo must be 403 or 404
        checkUnauthorizedError(() -> template.exchange(fullUrl(FILTER_USERS_URL), HttpMethod.POST,
                new HttpEntity<>(createUserFilterDto(), login(email2)), UsersPage.class));
    }
}
