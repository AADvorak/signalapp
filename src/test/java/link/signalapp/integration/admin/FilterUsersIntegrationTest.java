package link.signalapp.integration.admin;

import link.signalapp.dto.request.UserFilterDto;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.model.User;
import link.signalapp.security.PasswordEncoder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FilterUsersIntegrationTest extends AdminIntegrationTestBase {

    private static final List<String> FIRST_NAMES = List.of("abc", "abcd", "abcde", "abcdef", "abcdefg",
            "bcdefgh", "cdefgh", "defgh", "efgh", "fgh");
    private static final List<String> LAST_NAMES = List.of("fgh", "fghi", "fghij", "fghijk", "fghijkl",
            "ghijklm", "hijklm", "ijklm", "jklm", "klm");
    private static final List<String> PATRONYMICS = List.of("klm", "klmn", "klmno", "klmnop", "klmnopq",
            "lmnopqr", "mnopqr", "nopqr", "opqr", "pgr");

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
        registerUsers();
        createUsersForFilterTestsInDB();
        giveAdminRoleToUser(email1);
        giveAdminRoleToUser(email2);
    }

    @Test
    public void filterAll() {
        UserFilterDto userFilterDto = createUserFilterDto();
        filterAndCheckCounts(userFilterDto, 12, 2, 10);
    }

    @Test
    public void filterSearchVariant1() {
        UserFilterDto userFilterDto = createUserFilterDto()
                .setSearch("abc");
        filterAndCheckCounts(userFilterDto, 5, 1, 5);
    }

    @Test
    public void filterSearchVariant2() {
        UserFilterDto userFilterDto = createUserFilterDto()
                .setSearch("fgh");
        filterAndCheckCounts(userFilterDto, 10, 1, 10);
    }

    @Test
    public void filterSearchVariant3() {
        UserFilterDto userFilterDto = createUserFilterDto()
                .setSearch("klmnopq");
        filterAndCheckCounts(userFilterDto, 1, 1, 1);
    }

    @Test
    public void filterSearchVariant4() {
        UserFilterDto userFilterDto = createUserFilterDto()
                .setSearch("filter0");
        filterAndCheckCounts(userFilterDto, 1, 1, 1);
    }

    @Test
    public void filterRoleIds() {
        UserFilterDto userFilterDto = createUserFilterDto()
                .setRoleIds(List.of(getAdminRole().getId()));
        filterAndCheckCounts(userFilterDto, 2, 1, 2);
    }

    @Test
    public void filterSearchAndRoleIdsVariant1() {
        UserFilterDto userFilterDto = createUserFilterDto()
                .setSearch("abc")
                .setRoleIds(List.of(getAdminRole().getId()));
        filterAndCheckCounts(userFilterDto, 0, 0, 0);
    }

    @Test
    public void filterSearchAndRoleIdsVariant2() {
        UserFilterDto userFilterDto = createUserFilterDto()
                .setSearch(email1)
                .setRoleIds(List.of(getAdminRole().getId()));
        filterAndCheckCounts(userFilterDto, 1, 1, 1);
    }

    @Test
    public void filterSearchAndRoleIdsVariant3() {
        UserFilterDto userFilterDto = createUserFilterDto()
                .setSearch("first")
                .setRoleIds(List.of(getAdminRole().getId()));
        filterAndCheckCounts(userFilterDto, 2, 1, 2);
    }

    @Test
    public void sortDefault() {
        filterAndCheckSort(createUserFilterDto(),
                Comparator.comparing(UserDtoResponse::getCreateTime).reversed());
    }

    @Test
    public void sortByCreateTimeAsc() {
        filterAndCheckSort(createUserFilterDto().setSortBy("createTime").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getCreateTime));
    }

    @Test
    public void sortByCreateTimeDesc() {
        filterAndCheckSort(createUserFilterDto().setSortBy("createTime").setSortDir("desc"),
                Comparator.comparing(UserDtoResponse::getCreateTime).reversed());
    }

    @Test
    public void sortByFirstNameAsc() {
        filterAndCheckSort(createUserFilterDto().setSortBy("firstName").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getFirstName));
    }

    @Test
    public void sortByFirstNameDesc() {
        filterAndCheckSort(createUserFilterDto().setSortBy("firstName").setSortDir("desc"),
                Comparator.comparing(UserDtoResponse::getFirstName).reversed());
    }

    @Test
    public void sortByLastNameAsc() {
        filterAndCheckSort(createUserFilterDto().setSortBy("lastName").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getLastName));
    }

    @Test
    public void sortByLastNameDesc() {
        filterAndCheckSort(createUserFilterDto().setSortBy("lastName").setSortDir("desc"),
                Comparator.comparing(UserDtoResponse::getLastName).reversed());
    }

    @Test
    public void sortByPatronymicAsc() {
        filterAndCheckSort(createUserFilterDto().setSortBy("patronymic").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getPatronymic));
    }

    @Test
    public void sortByPatronymicDesc() {
        filterAndCheckSort(createUserFilterDto().setSortBy("patronymic").setSortDir("desc"),
                Comparator.comparing(UserDtoResponse::getPatronymic).reversed());
    }

    @Test
    public void sortByEmailAsc() {
        filterAndCheckSort(createUserFilterDto().setSortBy("email").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getEmail));
    }

    @Test
    public void sortByEmailDesc() {
        filterAndCheckSort(createUserFilterDto().setSortBy("email").setSortDir("desc"),
                Comparator.comparing(UserDtoResponse::getEmail).reversed());
    }

    @Test
    public void sortByWrongField() {
        filterAndCheckSort(createUserFilterDto().setSortBy("wrongField").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getCreateTime));
    }

    @Test
    public void sortByWrongDirection() {
        filterAndCheckSort(createUserFilterDto().setSortBy("firstName").setSortDir("wrongDirection"),
                Comparator.comparing(UserDtoResponse::getFirstName).reversed());
    }

    private void createUsersForFilterTestsInDB() {
        PasswordEncoder passwordEncoder = new PasswordEncoder(applicationProperties);
        for (int i = 0; i < FIRST_NAMES.size(); i++) {
            userRepository.save(new User()
                    .setFirstName(FIRST_NAMES.get(i))
                    .setLastName(LAST_NAMES.get(i))
                    .setPatronymic(PATRONYMICS.get(i))
                    .setEmail("filter" + i + "@test")
                    .setPassword(passwordEncoder.encode(password))
            );
        }
    }

    private void filterAndCheckCounts(UserFilterDto userFilterDto,
                                      long expectedElements, long expectedPages, long expectedDataSize) {
        UsersPage usersPage = getPageAndCheck(userFilterDto);
        assertAll(
                () -> assertEquals(expectedElements, usersPage.getElements()),
                () -> assertEquals(expectedPages, usersPage.getPages()),
                () -> assertEquals(expectedDataSize, usersPage.getData().size())
        );
    }

    private void filterAndCheckSort(UserFilterDto userFilterDto, Comparator<UserDtoResponse> expectedSortComparator) {
        UsersPage usersPage = getPageAndCheck(userFilterDto);
        List<Integer> actualIds = usersPage.getData().stream()
                .map(UserDtoResponse::getId).toList();
        List<Integer> expectedIds = usersPage.getData().stream()
                .sorted(expectedSortComparator)
                .map(UserDtoResponse::getId).toList();
        assertEquals(expectedIds, actualIds);
    }

    private UsersPage getPageAndCheck(UserFilterDto userFilterDto) {
        ResponseEntity<UsersPage> response = template.exchange(fullUrl(FILTER_USERS_URL), HttpMethod.POST,
                new HttpEntity<>(userFilterDto, login(email1)), UsersPage.class);
        UsersPage usersPage = response.getBody();
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(usersPage)
        );
        return usersPage;
    }
}
