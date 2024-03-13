package link.signalapp.integration.signals;

import com.fasterxml.jackson.core.JsonProcessingException;
import link.signalapp.dto.request.SignalFilterDto;
import link.signalapp.dto.response.ResponseWithTotalCounts;
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
        SignalFilterDto signalFilterDto = createSignalFilterDto();
        filterAndCheckCounts(signalFilterDto, 40, 4, 10);
    }

    @Test
    public void filterSearchVariant1() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setSearch("abc");
        filterAndCheckCounts(signalFilterDto, 20, 2, 10);
    }

    @Test
    public void filterSearchVariant2() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setSearch("abcdef");
        filterAndCheckCounts(signalFilterDto, 8, 1, 8);
    }

    @Test
    public void filterSearchVariant3() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setSearch("fgh");
        filterAndCheckCounts(signalFilterDto, 40, 4, 10);
    }

    @Test
    public void filterSearchVariant4() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setSearch("klm");
        filterAndCheckCounts(signalFilterDto, 20, 2, 10);
    }

    @Test
    public void filterSearchVariant5() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setSearch("lmn");
        filterAndCheckCounts(signalFilterDto, 0, 0, 0);
    }

    @Test
    public void filterSampleRatesVariant1() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setSampleRates(toBigDecimalList(Stream.of(3000)));
        filterAndCheckCounts(signalFilterDto, 8, 1, 8);
    }

    @Test
    public void filterSampleRatesVariant2() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setSampleRates(toBigDecimalList(Stream.of(3000, 8000)));
        filterAndCheckCounts(signalFilterDto, 16, 2, 10);
    }

    @Test
    public void filterSampleRatesVariant3() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setSampleRates(toBigDecimalList(Stream.of(3000, 4000)));
        filterAndCheckCounts(signalFilterDto, 8, 1, 8);
    }

    @Test
    public void filterSampleRatesVariant4() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setSampleRates(toBigDecimalList(Stream.of(9000, 4000)));
        filterAndCheckCounts(signalFilterDto, 0, 0, 0);
    }

    @Test
    public void filterFoldersVariant1() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setFolderIds(toFolderIdList(List.of(0)));
        filterAndCheckCounts(signalFilterDto, 20, 2, 10);
    }

    @Test
    public void filterFoldersVariant2() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setFolderIds(toFolderIdList(List.of(1)));
        filterAndCheckCounts(signalFilterDto, 10, 1, 10);
    }

    @Test
    public void filterFoldersVariant3() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setFolderIds(toFolderIdList(List.of(2)));
        filterAndCheckCounts(signalFilterDto, 10, 1, 10);
    }

    @Test
    public void filterFoldersVariant4() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setFolderIds(toFolderIdList(List.of(0, 1)));
        filterAndCheckCounts(signalFilterDto, 30, 3, 10);
    }

    @Test
    public void filterFoldersVariant5() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setFolderIds(toFolderIdList(List.of(0, 2)));
        filterAndCheckCounts(signalFilterDto, 30, 3, 10);
    }

    @Test
    public void filterFoldersVariant6() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setFolderIds(toFolderIdList(List.of(1, 2)));
        filterAndCheckCounts(signalFilterDto, 10, 1, 10);
    }

    @Test
    public void filterSearchAndSampleRates() {

    }

    @Test
    public void filterSampleRatesAndFolders() {

    }

    @Test
    public void filterSearchAndFolders() {

    }

    @Test
    public void filterSearchAndSampleRatesAndFolders() {

    }

    @Test
    public void sortDefault() {
        filterAndCheckSort(createSignalFilterDto(), Comparator.comparing(SignalDtoResponse::getId).reversed());
    }

    @Test
    public void sortByNameAsc() {
        filterAndCheckSort(createSignalFilterDto().setSortBy("name").setSortDir("asc"),
                Comparator.comparing(SignalDtoResponse::getName));
    }

    @Test
    public void sortByNameDesc() {
        filterAndCheckSort(createSignalFilterDto().setSortBy("name").setSortDir("desc"),
                Comparator.comparing(SignalDtoResponse::getName).reversed());
    }

    @Test
    public void sortByDescriptionAsc() {
        filterAndCheckSort(createSignalFilterDto().setSortBy("description").setSortDir("asc"),
                Comparator.comparing(SignalDtoResponse::getDescription));
    }

    @Test
    public void sortByDescriptionDesc() {
        filterAndCheckSort(createSignalFilterDto().setSortBy("description").setSortDir("desc"),
                Comparator.comparing(SignalDtoResponse::getDescription).reversed());
    }

    @Test
    public void sortBySampleRateAsc() {
        filterAndCheckSort(createSignalFilterDto().setSortBy("sampleRate").setSortDir("asc"),
                Comparator.comparing(SignalDtoResponse::getSampleRate));
    }

    @Test
    public void sortBySampleRateDesc() {
        filterAndCheckSort(createSignalFilterDto().setSortBy("sampleRate").setSortDir("desc"),
                Comparator.comparing(SignalDtoResponse::getSampleRate).reversed());
    }

    @Test
    public void sortByWrongField() {
        filterAndCheckSort(createSignalFilterDto().setSortBy("wrongField").setSortDir("asc"),
                Comparator.comparing(SignalDtoResponse::getId));
    }

    @Test
    public void sortByWrongDirection() {
        filterAndCheckSort(createSignalFilterDto().setSortBy("name").setSortDir("wrongDirection"),
                Comparator.comparing(SignalDtoResponse::getName).reversed());
    }

    @Test
    public void filterWrongPage() throws JsonProcessingException {
        SignalFilterDto signalFilterDto = createSignalFilterDto().setPage(-1);
        checkBadRequestFieldError(() -> template.exchange(fullUrl(FILTER_SIGNALS_URL), HttpMethod.POST,
                new HttpEntity<>(signalFilterDto, login(email1)), SignalsPage.class),
                "PositiveOrZero", "page");
    }

    @Test
    public void filterWrongSize() throws JsonProcessingException {
        SignalFilterDto signalFilterDto = createSignalFilterDto().setSize(30);
        checkBadRequestFieldError(() -> template.exchange(fullUrl(FILTER_SIGNALS_URL), HttpMethod.POST,
                        new HttpEntity<>(signalFilterDto, login(email1)), SignalsPage.class),
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

    private void filterAndCheckCounts(SignalFilterDto signalFilterDto,
                                      long expectedElements, long expectedPages, long expectedDataSize) {
        SignalsPage signalsPage = getSignalsPageAndCheck(signalFilterDto);
        assertAll(
                () -> assertEquals(expectedElements, signalsPage.getElements()),
                () -> assertEquals(expectedPages, signalsPage.getPages()),
                () -> assertEquals(expectedDataSize, signalsPage.getData().size())
        );
    }

    private void filterAndCheckSort(SignalFilterDto signalFilterDto, Comparator<SignalDtoResponse> expectedSortComparator) {
        SignalsPage signalsPage = getSignalsPageAndCheck(signalFilterDto);
        List<Integer> actualIds = signalsPage.getData().stream()
                .map(SignalDtoResponse::getId).toList();
        List<Integer> expectedIds = signalsPage.getData().stream()
                .sorted(expectedSortComparator)
                .map(SignalDtoResponse::getId).toList();
        assertEquals(expectedIds, actualIds);
    }

    private SignalsPage getSignalsPageAndCheck(SignalFilterDto signalFilterDto) {
        ResponseEntity<SignalsPage> response = template.exchange(fullUrl(FILTER_SIGNALS_URL), HttpMethod.POST,
                new HttpEntity<>(signalFilterDto, login(email1)), SignalsPage.class);
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

    private SignalFilterDto createSignalFilterDto() {
        return new SignalFilterDto().setPage(0).setSize(10);
    }

    private List<BigDecimal> toBigDecimalList(Stream<Integer> stream) {
        return stream.map(BigDecimal::valueOf).toList();
    }

    private List<Integer> toFolderIdList(List<Integer> folderNumbers) {
        return folderNumbers.stream()
                .map(number -> folders.get(number).getId())
                .toList();
    }

    private static final class SignalsPage extends ResponseWithTotalCounts<SignalDtoResponse> {
    }
}
