package link.signalapp.integration.admin;

import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.model.Role;
import link.signalapp.model.Signal;
import link.signalapp.model.User;
import link.signalapp.model.UserToken;
import link.signalapp.repository.SignalRepository;
import link.signalapp.repository.UserTokenRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminGetUserInfoIntegrationTest extends AdminIntegrationTestBase {

    private static final int SECOND_USER_SIGNALS_NUMBER = 5;

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
        registerUsers();
        giveAdminRoleToUser(email1);
        signalRepository.deleteAll();
        createSignalsInDB();
    }

    @Test
    public void getFirstUserInfo() {
        ResponseEntity<UsersPage> response = template.exchange(fullUrl(FILTER_USERS_URL), HttpMethod.POST,
                new HttpEntity<>(createRequest().setSearch(email1), login(email1)), UsersPage.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody()).getData().get(0);
        User user = userRepository.findByEmail(email1);
        UserToken userToken = userTokenRepository.findLastByUserId(user.getId());
        assertNotNull(user.getRoles());
        assertAll(
                () -> assertEquals(user.getId(), userDtoResponse.getId()),
                () -> assertEquals(user.getEmail(), userDtoResponse.getEmail()),
                () -> assertEquals(user.getFirstName(), userDtoResponse.getFirstName()),
                () -> assertEquals(user.getLastName(), userDtoResponse.getLastName()),
                () -> assertEquals(user.getPatronymic(), userDtoResponse.getPatronymic()),
                () -> assertEquals(user.getPatronymic(), userDtoResponse.getPatronymic()),
                () -> assertEquals(0, userDtoResponse.getStoredSignalsNumber()),
                () -> assertEquals(user.getCreateTime(), userDtoResponse.getCreateTime()),
                () -> assertEquals(userToken.getLastActionTime(), userDtoResponse.getLastActionTime()),
                () -> assertTrue(userHasRole(getAdminRole(), userDtoResponse))
        );
    }

    @Test
    public void getSecondUserInfo() {
        ResponseEntity<UsersPage> response = template.exchange(fullUrl(FILTER_USERS_URL), HttpMethod.POST,
                new HttpEntity<>(createRequest().setSearch(email2), login(email1)), UsersPage.class);
        UserDtoResponse userDtoResponse = Objects.requireNonNull(response.getBody()).getData().get(0);
        User user = userRepository.findByEmail(email2);
        UserToken userToken = userTokenRepository.findLastByUserId(user.getId());
        assertAll(
                () -> assertEquals(user.getId(), userDtoResponse.getId()),
                () -> assertEquals(user.getEmail(), userDtoResponse.getEmail()),
                () -> assertEquals(user.getFirstName(), userDtoResponse.getFirstName()),
                () -> assertEquals(user.getLastName(), userDtoResponse.getLastName()),
                () -> assertEquals(user.getPatronymic(), userDtoResponse.getPatronymic()),
                () -> assertEquals(user.getPatronymic(), userDtoResponse.getPatronymic()),
                () -> assertEquals(SECOND_USER_SIGNALS_NUMBER, userDtoResponse.getStoredSignalsNumber()),
                () -> assertEquals(user.getCreateTime(), userDtoResponse.getCreateTime()),
                () -> assertEquals(userToken.getLastActionTime(), userDtoResponse.getLastActionTime()),
                () -> assertFalse(userHasRole(getAdminRole(), userDtoResponse))
        );
    }

    private void createSignalsInDB() {
        int userId = userRepository.findByEmail(email2).getId();
        for (int i = 1; i <= SECOND_USER_SIGNALS_NUMBER; i++) {
            signalRepository.save(new Signal()
                    .setName("name" + i)
                    .setDescription("description" + i)
                    .setSampleRate(BigDecimal.valueOf(8000))
                    .setXMin(BigDecimal.ZERO)
                    .setMaxAbsY(BigDecimal.ONE)
                    .setUserId(userId));
        }
    }

    private boolean userHasRole(Role role, UserDtoResponse user) {
        return user.getRoles().stream().anyMatch(roleDtoResponse -> roleDtoResponse.getId() == role.getId());
    }
}
