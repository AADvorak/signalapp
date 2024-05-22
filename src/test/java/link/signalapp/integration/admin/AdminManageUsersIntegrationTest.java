package link.signalapp.integration.admin;

import link.signalapp.model.Role;
import link.signalapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class AdminManageUsersIntegrationTest extends AdminIntegrationTestBase {

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        registerUsers();
        giveAdminRoleToUser(email1);
    }

    @Test
    public void deleteUserOk() {
        int userId = secondUserId();
        ResponseEntity<Void> response = template.exchange(fullUrl(userPath(userId)), HttpMethod.DELETE,
                new HttpEntity<>(login(email1)), Void.class);
        User deletedUser = userRepository.findByEmail(email2);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNull(deletedUser)
        );
    }

    @Test
    public void deleteUserNotFound() {
        checkNotFoundError(() -> template.exchange(fullUrl(userPath(notExistingUserId())), HttpMethod.DELETE,
                new HttpEntity<>(login(email1)), Void.class));
    }

    @Test
    public void deleteUserRoleOk() {
        giveAdminRoleToUser(email2);
        ResponseEntity<Void> response = template.exchange(fullUrl(userRolePath(secondUserId(), getAdminRole().getId())),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), Void.class);
        Role deletedRole = findUserRoleByRoleId(userRepository.findByEmail(email2), getAdminRole().getId());
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNull(deletedRole)
        );
    }

    @Test
    public void deleteUserRoleNotFoundUser() {
        checkNotFoundError(() -> template.exchange(fullUrl(userRolePath(notExistingUserId(), getAdminRole().getId())),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), Void.class));
    }

    @Test
    public void deleteUserRoleNotFoundRole() {
        checkNotFoundError(() -> template.exchange(fullUrl(userRolePath(secondUserId(), getAdminRole().getId())),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), Void.class));
    }

    @Test
    public void setUserRoleOk() {
        int roleId = getAdminRole().getId();
        ResponseEntity<Void> response = template.exchange(fullUrl(userRolePath(secondUserId(), roleId)),
                HttpMethod.PUT, new HttpEntity<>(login(email1)), Void.class);
        Role userRole = findUserRoleByRoleId(userRepository.findByEmail(email2), roleId);
        assertNotNull(userRole);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(roleId, userRole.getId())
        );
    }

    @Test
    public void setUserRoleNotFoundUser() {
        checkNotFoundError(() -> template.exchange(fullUrl(userRolePath(notExistingUserId(), getAdminRole().getId())),
                HttpMethod.PUT, new HttpEntity<>(login(email1)), Void.class));
    }

    @Test
    public void setUserRoleNotFoundRole() {
        checkNotFoundError(() -> template.exchange(fullUrl(userRolePath(secondUserId(), notExistingRoleId())),
                HttpMethod.PUT, new HttpEntity<>(login(email1)), Void.class));
    }

    private int secondUserId() {
        return userRepository.findByEmail(email2).getId();
    }

    private int notExistingUserId() {
        return userRepository.findAll().stream()
                .map(User::getId)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
    }

    private int notExistingRoleId() {
        return roleRepository.findAll().stream()
                .map(Role::getId)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
    }

    private Role findUserRoleByRoleId(User user, int roleId) {
        return user.getRoles().stream().filter(role -> role.getId() == roleId).findFirst().orElse(null);
    }

    private String userRolePath(int userId, int roleId) {
        return userPath(userId) + "/roles/" + roleId;
    }

    private String userPath(int userId) {
        return "/api/admin/users/" + userId;
    }
}
