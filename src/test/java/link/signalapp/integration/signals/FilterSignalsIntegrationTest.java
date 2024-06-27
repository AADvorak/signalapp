package link.signalapp.integration.signals;

import link.signalapp.dto.request.SignalsPageDtoRequest;
import link.signalapp.dto.response.PageDtoResponse;
import link.signalapp.dto.response.SignalDtoResponse;
import link.signalapp.integration.IntegrationTestBase;
import link.signalapp.model.Folder;
import link.signalapp.model.Signal;
import link.signalapp.repository.FolderRepository;
import link.signalapp.repository.SignalRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FilterSignalsIntegrationTest extends IntegrationTestBase {

    private static final String FILTER_SIGNALS_URL = "/api/signals/filter";

    private static final List<String> NAMES = List.of("abc", "abcd", "abcde", "abcdef", "abcdefg",
            "bcdefgh", "cdefgh", "defgh", "efgh", "fgh");
    private static final List<String> DESCRIPTIONS = List.of("fgh", "fghi", "fghij", "fghijk", "fghijkl",
            "ghijklm", "hijklm", "ijklm", "jklm", "klm");
    private static final List<Integer> SAMPLE_RATES = List.of(3000, 8000, 16000, 22000, 44000,
            3000, 8000, 16000, 22000, 44000);

    private final List<Folder> folders = new ArrayList<>();

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private FolderRepository folderRepository;

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
        registerUsers();
        int userId = userRepository.findByEmail(email1).getId();
        folderRepository.deleteAll();
        createFoldersInDB(userId);
        signalRepository.deleteAll();
        createSignalsInDB(userId);
    }

    @Test
    public void filterAll() {
        SignalsPageDtoRequest request = createRequest();
        filterAndCheckCounts(request, 40, 4);
    }

    @Test
    public void filterSearchVariant1() {
        SignalsPageDtoRequest request = createRequest()
                .setSearch("abc");
        filterAndCheckCounts(request, 20, 2);
    }

    @Test
    public void filterSearchVariant2() {
        SignalsPageDtoRequest request = createRequest()
                .setSearch("abcdef");
        filterAndCheckCounts(request, 8, 1);
    }

    @Test
    public void filterSearchVariant3() {
        SignalsPageDtoRequest request = createRequest()
                .setSearch("fgh");
        filterAndCheckCounts(request, 40, 4);
    }

    @Test
    public void filterSearchVariant4() {
        SignalsPageDtoRequest request = createRequest()
                .setSearch("klm");
        filterAndCheckCounts(request, 20, 2);
    }

    @Test
    public void filterSearchVariant5() {
        SignalsPageDtoRequest request = createRequest()
                .setSearch("lmn");
        filterAndCheckCounts(request, 0, 0);
    }

    @Test
    public void filterSampleRatesVariant1() {
        SignalsPageDtoRequest request = createRequest()
                .setSampleRates(toBigDecimalList(Stream.of(3000)));
        filterAndCheckCounts(request, 8, 1);
    }

    @Test
    public void filterSampleRatesVariant2() {
        SignalsPageDtoRequest request = createRequest()
                .setSampleRates(toBigDecimalList(Stream.of(3000, 8000)));
        filterAndCheckCounts(request, 16, 2);
    }

    @Test
    public void filterSampleRatesVariant3() {
        SignalsPageDtoRequest request = createRequest()
                .setSampleRates(toBigDecimalList(Stream.of(3000, 4000)));
        filterAndCheckCounts(request, 8, 1);
    }

    @Test
    public void filterSampleRatesVariant4() {
        SignalsPageDtoRequest request = createRequest()
                .setSampleRates(toBigDecimalList(Stream.of(9000, 4000)));
        filterAndCheckCounts(request, 0, 0);
    }

    @Test
    public void filterFoldersVariant1() {
        SignalsPageDtoRequest request = createRequest()
                .setFolderIds(toFolderIdList(List.of(0)));
        filterAndCheckCounts(request, 20, 2);
    }

    @Test
    public void filterFoldersVariant2() {
        SignalsPageDtoRequest request = createRequest()
                .setFolderIds(toFolderIdList(List.of(1)));
        filterAndCheckCounts(request, 10, 1);
    }

    @Test
    public void filterFoldersVariant3() {
        SignalsPageDtoRequest request = createRequest()
                .setFolderIds(toFolderIdList(List.of(2)));
        filterAndCheckCounts(request, 10, 1);
    }

    @Test
    public void filterFoldersVariant4() {
        SignalsPageDtoRequest request = createRequest()
                .setFolderIds(toFolderIdList(List.of(0, 1)));
        filterAndCheckCounts(request, 30, 3);
    }

    @Test
    public void filterFoldersVariant5() {
        SignalsPageDtoRequest request = createRequest()
                .setFolderIds(toFolderIdList(List.of(0, 2)));
        filterAndCheckCounts(request, 30, 3);
    }

    @Test
    public void filterFoldersVariant6() {
        SignalsPageDtoRequest request = createRequest()
                .setFolderIds(toFolderIdList(List.of(1, 2)));
        filterAndCheckCounts(request, 10, 1);
    }

    @Test
    public void filterSearchAndSampleRatesVariant1() {
        SignalsPageDtoRequest request = createRequest()
                .setSearch("abc")
                .setSampleRates(toBigDecimalList(Stream.of(3000, 8000)));
        filterAndCheckCounts(request, 8, 1);
    }

    @Test
    public void filterSearchAndSampleRatesVariant2() {
        SignalsPageDtoRequest request = createRequest()
                .setSearch("abc")
                .setSampleRates(toBigDecimalList(Stream.of(44000)));
        filterAndCheckCounts(request, 4, 1);
    }

    @Test
    public void filterSearchAndSampleRatesVariant3() {
        SignalsPageDtoRequest request = createRequest()
                .setSearch("abcdef")
                .setSampleRates(toBigDecimalList(Stream.of(3000, 8000)));
        filterAndCheckCounts(request, 0, 0);
    }

    @Test
    public void filterSampleRatesAndFolders() {
        SignalsPageDtoRequest request = createRequest()
                .setSampleRates(toBigDecimalList(Stream.of(3000, 8000)))
                .setFolderIds(toFolderIdList(List.of(2)));
        filterAndCheckCounts(request, 4, 1);
    }

    @Test
    public void filterSearchAndFolders() {
        SignalsPageDtoRequest request = createRequest()
                .setSearch("abc")
                .setFolderIds(toFolderIdList(List.of(2)));
        filterAndCheckCounts(request, 5, 1);
    }

    @Test
    public void filterSearchAndSampleRatesAndFolders() {
        SignalsPageDtoRequest request = createRequest()
                .setSearch("abc")
                .setSampleRates(toBigDecimalList(Stream.of(3000)))
                .setFolderIds(toFolderIdList(List.of(2)));
        filterAndCheckCounts(request, 1, 1);
    }

    @Test
    public void sortDefault() {
        filterAndCheckSort(createRequest(), Comparator.comparing(SignalDtoResponse::getId).reversed());
    }

    @Test
    public void sortByNameAsc() {
        filterAndCheckSort(createRequest().setSortBy("name").setSortDir("asc"),
                Comparator.comparing(SignalDtoResponse::getName));
    }

    @Test
    public void sortByNameDesc() {
        filterAndCheckSort(createRequest().setSortBy("name").setSortDir("desc"),
                Comparator.comparing(SignalDtoResponse::getName).reversed());
    }

    @Test
    public void sortByDescriptionAsc() {
        filterAndCheckSort(createRequest().setSortBy("description").setSortDir("asc"),
                Comparator.comparing(SignalDtoResponse::getDescription));
    }

    @Test
    public void sortByDescriptionDesc() {
        filterAndCheckSort(createRequest().setSortBy("description").setSortDir("desc"),
                Comparator.comparing(SignalDtoResponse::getDescription).reversed());
    }

    @Test
    public void sortBySampleRateAsc() {
        filterAndCheckSort(createRequest().setSortBy("sampleRate").setSortDir("asc"),
                Comparator.comparing(SignalDtoResponse::getSampleRate));
    }

    @Test
    public void sortBySampleRateDesc() {
        filterAndCheckSort(createRequest().setSortBy("sampleRate").setSortDir("desc"),
                Comparator.comparing(SignalDtoResponse::getSampleRate).reversed());
    }

    @Test
    public void sortByWrongField() {
        filterAndCheckSort(createRequest().setSortBy("wrongField").setSortDir("asc"),
                Comparator.comparing(SignalDtoResponse::getId));
    }

    @Test
    public void sortByWrongDirection() {
        filterAndCheckSort(createRequest().setSortBy("name").setSortDir("wrongDirection"),
                Comparator.comparing(SignalDtoResponse::getName).reversed());
    }

    @Test
    public void filterWrongPage() {
        SignalsPageDtoRequest request = createRequest().setPage(-1);
        checkBadRequestFieldError(() -> template.exchange(fullUrl(FILTER_SIGNALS_URL), HttpMethod.POST,
                new HttpEntity<>(request, login(email1)), SignalsPage.class),
                "PositiveOrZero", "page");
    }

    @Test
    public void filterWrongSize() {
        SignalsPageDtoRequest request = createRequest().setSize(30);
        checkBadRequestFieldError(() -> template.exchange(fullUrl(FILTER_SIGNALS_URL), HttpMethod.POST,
                        new HttpEntity<>(request, login(email1)), SignalsPage.class),
                "Max", "size");
    }

    @Test
    public void getSampleRates() {
        ResponseEntity<BigDecimal[]> response = template.exchange(fullUrl("/api/signals/sample-rates"),
                HttpMethod.GET, new HttpEntity<>(login(email1)), BigDecimal[].class);
        BigDecimal[] sampleRates = response.getBody();
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(sampleRates)
        );
        assertEquals(List.of(3000, 8000, 16000, 22000, 44000),
                Arrays.stream(sampleRates).sorted().map(BigDecimal::intValue).toList());
    }

    private void filterAndCheckCounts(SignalsPageDtoRequest request,
                                      long expectedElements, long expectedPages) {
        while (request.getPage() < expectedPages) {
            SignalsPage signalsPage = getSignalsPageAndCheck(request);
            long offsetDataSize = expectedElements - (long) request.getPage() * request.getSize();
            long expectedPageDataSize = offsetDataSize > request.getSize() ? request.getSize() : offsetDataSize;
            assertAll(
                    () -> assertEquals(expectedElements, signalsPage.getElements()),
                    () -> assertEquals(expectedPages, signalsPage.getPages()),
                    () -> assertEquals(expectedPageDataSize, signalsPage.getData().size())
            );
            request.setPage(request.getPage() + 1);
        }
    }

    private void filterAndCheckSort(SignalsPageDtoRequest request, Comparator<SignalDtoResponse> expectedSortComparator) {
        SignalsPage signalsPage = getSignalsPageAndCheck(request);
        List<Integer> actualIds = signalsPage.getData().stream()
                .map(SignalDtoResponse::getId).toList();
        List<Integer> expectedIds = signalsPage.getData().stream()
                .sorted(expectedSortComparator)
                .map(SignalDtoResponse::getId).toList();
        assertEquals(expectedIds, actualIds);
    }

    private SignalsPage getSignalsPageAndCheck(SignalsPageDtoRequest request) {
        ResponseEntity<SignalsPage> response = template.exchange(fullUrl(FILTER_SIGNALS_URL), HttpMethod.POST,
                new HttpEntity<>(request, login(email1)), SignalsPage.class);
        SignalsPage signalsPage = response.getBody();
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(signalsPage)
        );
        return signalsPage;
    }

    private void createFoldersInDB(int userId) {
        for (int i = 1; i <= 3; i++) {
            Folder folder = folderRepository.save(new Folder()
                    .setName("Folder" + i)
                    .setDescription("description" + i)
                    .setUserId(userId));
            folders.add(folder);
        }
    }

    private void createSignalsInDB(int userId) {
        for (int i = 0; i < 4; i++) {
            Set<Folder> currentFolders = getPartOfFolders(i);
            for (int j = 0; j < 10; j++) {
                signalRepository.save(new Signal()
                        .setName(NAMES.get(j))
                        .setDescription(DESCRIPTIONS.get(j))
                        .setSampleRate(BigDecimal.valueOf(SAMPLE_RATES.get(j)))
                        .setXMin(BigDecimal.ZERO)
                        .setMaxAbsY(BigDecimal.ONE)
                        .setUserId(userId)
                        .setFolders(currentFolders));
            }
        }
    }

    private Set<Folder> getPartOfFolders(int number) {
        return switch (number) {
            case 1, 2 -> Set.of(folders.get(0));
            case 3 -> Set.of(folders.get(1), folders.get(2));
            default -> new HashSet<>();
        };
    }

    private SignalsPageDtoRequest createRequest() {
        return new SignalsPageDtoRequest().setPage(0).setSize(10);
    }

    private List<BigDecimal> toBigDecimalList(Stream<Integer> stream) {
        return stream.map(BigDecimal::valueOf).toList();
    }

    private List<Integer> toFolderIdList(List<Integer> folderNumbers) {
        return folderNumbers.stream()
                .map(number -> folders.get(number).getId())
                .toList();
    }

    private static final class SignalsPage extends PageDtoResponse<SignalDtoResponse> {
    }
}
