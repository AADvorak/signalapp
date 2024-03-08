package link.signalapp.integration.signals;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        filterAndCheckCounts(signalFilterDto, 30, 3, 10);
    }

    @Test
    public void filterFoldersVariant2() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setFolderIds(toFolderIdList(List.of(1)));
        filterAndCheckCounts(signalFilterDto, 20, 2, 10);
    }

    @Test
    public void filterFoldersVariant3() {
        SignalFilterDto signalFilterDto = createSignalFilterDto()
                .setFolderIds(toFolderIdList(List.of(2)));
        filterAndCheckCounts(signalFilterDto, 10, 1, 10);
    }

    private void filterAndCheckCounts(SignalFilterDto signalFilterDto,
                                      long expectedElements, long expectedPages, long expectedDataSize) {
        ResponseEntity<SignalsPage> response = template.exchange(fullUrl(FILTER_SIGNALS_URL), HttpMethod.POST,
                new HttpEntity<>(signalFilterDto, login(email1)), SignalsPage.class);
        SignalsPage signalsPage = response.getBody();
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(signalsPage)
        );
        assertAll(
                () -> assertEquals(expectedElements, signalsPage.getElements()),
                () -> assertEquals(expectedPages, signalsPage.getPages()),
                () -> assertEquals(expectedDataSize, signalsPage.getData().size())
        );
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
        Set<Folder> currentFolders = new HashSet<>();
        for (int i = 0; i < number; i++) {
            currentFolders.add(folders.get(i));
        }
        return currentFolders;
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
