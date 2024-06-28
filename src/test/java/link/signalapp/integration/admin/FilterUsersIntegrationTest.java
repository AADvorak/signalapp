package link.signalapp.integration.admin;

import link.signalapp.dto.request.paging.UsersPageDtoRequest;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.model.Role;
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
        giveRoleToUser(email2, getRoleByName(Role.EXTENDED_STORAGE));
    }

    @Test
    public void filterAll() {
        UsersPageDtoRequest request = createRequest();
        filterAndCheckCounts(request, 12, 2);
    }

    @Test
    public void filterSearchVariant1() {
        UsersPageDtoRequest request = createRequest()
                .setSearch("abc");
        filterAndCheckCounts(request, 5, 1);
    }

    @Test
    public void filterSearchVariant2() {
        UsersPageDtoRequest request = createRequest()
                .setSearch("fgh");
        filterAndCheckCounts(request, 10, 1);
    }

    @Test
    public void filterSearchVariant3() {
        UsersPageDtoRequest request = createRequest()
                .setSearch("klmnopq");
        filterAndCheckCounts(request, 1, 1);
    }

    @Test
    public void filterSearchVariant4() {
        UsersPageDtoRequest request = createRequest()
                .setSearch("filter0");
        filterAndCheckCounts(request, 1, 1);
    }

    @Test
    public void filterRoleIdsVariant1() {
        UsersPageDtoRequest request = createRequest()
                .setRoleIds(List.of(getAdminRole().getId()));
        filterAndCheckCounts(request, 2, 1);
    }

    @Test
    public void filterRoleIdsVariant2() {
        UsersPageDtoRequest request = createRequest()
                .setRoleIds(List.of(getRoleByName(Role.EXTENDED_STORAGE).getId()));
        filterAndCheckCounts(request, 1, 1);
    }

    @Test
    public void filterRoleIdsVariant3() {
        UsersPageDtoRequest request = createRequest()
                .setRoleIds(List.of(getAdminRole().getId(), getRoleByName(Role.EXTENDED_STORAGE).getId()));
        filterAndCheckCounts(request, 2, 1);
    }

    @Test
    public void filterSearchAndRoleIdsVariant1() {
        UsersPageDtoRequest request = createRequest()
                .setSearch("abc")
                .setRoleIds(List.of(getAdminRole().getId()));
        filterAndCheckCounts(request, 0, 0);
    }

    @Test
    public void filterSearchAndRoleIdsVariant2() {
        UsersPageDtoRequest request = createRequest()
                .setSearch(email1)
                .setRoleIds(List.of(getAdminRole().getId()));
        filterAndCheckCounts(request, 1, 1);
    }

    @Test
    public void filterSearchAndRoleIdsVariant3() {
        UsersPageDtoRequest request = createRequest()
                .setSearch("first")
                .setRoleIds(List.of(getAdminRole().getId()));
        filterAndCheckCounts(request, 2, 1);
    }

    @Test
    public void sortDefault() {
        filterAndCheckSort(createRequest(),
                Comparator.comparing(UserDtoResponse::getCreateTime).reversed());
    }

    @Test
    public void sortByCreateTimeAsc() {
        filterAndCheckSort(createRequest().setSortBy("createTime").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getCreateTime));
    }

    @Test
    public void sortByCreateTimeDesc() {
        filterAndCheckSort(createRequest().setSortBy("createTime").setSortDir("desc"),
                Comparator.comparing(UserDtoResponse::getCreateTime).reversed());
    }

    @Test
    public void sortByFirstNameAsc() {
        filterAndCheckSort(createRequest().setSortBy("firstName").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getFirstName));
    }

    @Test
    public void sortByFirstNameDesc() {
        filterAndCheckSort(createRequest().setSortBy("firstName").setSortDir("desc"),
                Comparator.comparing(UserDtoResponse::getFirstName).reversed());
    }

    @Test
    public void sortByLastNameAsc() {
        filterAndCheckSort(createRequest().setSortBy("lastName").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getLastName));
    }

    @Test
    public void sortByLastNameDesc() {
        filterAndCheckSort(createRequest().setSortBy("lastName").setSortDir("desc"),
                Comparator.comparing(UserDtoResponse::getLastName).reversed());
    }

    @Test
    public void sortByPatronymicAsc() {
        filterAndCheckSort(createRequest().setSortBy("patronymic").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getPatronymic));
    }

    @Test
    public void sortByPatronymicDesc() {
        filterAndCheckSort(createRequest().setSortBy("patronymic").setSortDir("desc"),
                Comparator.comparing(UserDtoResponse::getPatronymic).reversed());
    }

    @Test
    public void sortByEmailAsc() {
        filterAndCheckSort(createRequest().setSortBy("email").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getEmail));
    }

    @Test
    public void sortByEmailDesc() {
        filterAndCheckSort(createRequest().setSortBy("email").setSortDir("desc"),
                Comparator.comparing(UserDtoResponse::getEmail).reversed());
    }

    @Test
    public void sortByWrongField() {
        filterAndCheckSort(createRequest().setSortBy("wrongField").setSortDir("asc"),
                Comparator.comparing(UserDtoResponse::getCreateTime));
    }

    @Test
    public void sortByWrongDirection() {
        filterAndCheckSort(createRequest().setSortBy("firstName").setSortDir("wrongDirection"),
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

    private void filterAndCheckCounts(UsersPageDtoRequest request,
                                      long expectedElements, long expectedPages) {
        while (request.getPage() < expectedPages) {
            UsersPage usersPage = getPageAndCheck(request);
            long offsetDataSize = expectedElements - (long) request.getPage() * request.getSize();
            long expectedPageDataSize = offsetDataSize > request.getSize() ? request.getSize() : offsetDataSize;
            assertAll(
                    () -> assertEquals(expectedElements, usersPage.getElements()),
                    () -> assertEquals(expectedPages, usersPage.getPages()),
                    () -> assertEquals(expectedPageDataSize, usersPage.getData().size())
            );
            request.setPage(request.getPage() + 1);
        }
    }

    private void filterAndCheckSort(UsersPageDtoRequest request, Comparator<UserDtoResponse> expectedSortComparator) {
        UsersPage usersPage = getPageAndCheck(request);
        List<Integer> actualIds = usersPage.getData().stream()
                .map(UserDtoResponse::getId).toList();
        List<Integer> expectedIds = usersPage.getData().stream()
                .sorted(expectedSortComparator)
                .map(UserDtoResponse::getId).toList();
        assertEquals(expectedIds, actualIds);
    }

    private UsersPage getPageAndCheck(UsersPageDtoRequest request) {
        ResponseEntity<UsersPage> response = template.exchange(fullUrl(USERS_PAGE_URL), HttpMethod.POST,
                new HttpEntity<>(request, login(email1)), UsersPage.class);
        UsersPage usersPage = response.getBody();
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(usersPage)
        );
        return usersPage;
    }
}
