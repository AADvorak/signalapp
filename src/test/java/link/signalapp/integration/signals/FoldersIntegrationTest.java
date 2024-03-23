package link.signalapp.integration.signals;

import link.signalapp.dto.request.FolderDtoRequest;
import link.signalapp.dto.response.FolderDtoResponse;
import link.signalapp.file.FileManager;
import link.signalapp.integration.IntegrationTestBase;
import link.signalapp.model.Folder;
import link.signalapp.model.Signal;
import link.signalapp.repository.FolderRepository;
import link.signalapp.repository.SignalRepository;
import link.signalapp.service.FolderService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FoldersIntegrationTest extends IntegrationTestBase {

    private static final String FOLDERS_URL = "/api/folders";

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private FileManager fileManager;

    private int userId;

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
        registerUsers();
        userId = userRepository.findByEmail(email1).getId();
    }

    @AfterAll
    public void afterAll() {
        fileManager.deleteDirRecursively(new File(applicationProperties.getDataPath() + "signals"));
    }

    @BeforeEach
    public void clearFoldersInDB() {
        folderRepository.deleteAll();
    }

    @Test
    public void getFolders() {
        int size = 3;
        Map<Integer, Folder> folders = createFoldersInDB(size);
        ResponseEntity<FolderDtoResponse[]> response = template.exchange(fullUrl(FOLDERS_URL),
                HttpMethod.GET, new HttpEntity<>(login(email1)), FolderDtoResponse[].class);
        FolderDtoResponse[] foldersResponse = response.getBody();
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(foldersResponse)
        );
        assertEquals(size, foldersResponse.length);
        for (FolderDtoResponse folderResponse : foldersResponse) {
            Folder folder = folders.get(folderResponse.getId());
            assertAll(
                    () -> assertEquals(folder.getName(), folderResponse.getName()),
                    () -> assertEquals(folder.getDescription(), folderResponse.getDescription())
            );
        }
    }

    @Test
    public void addOk() {
        FolderDtoRequest folderDtoRequest = createFolderDtoRequest();
        ResponseEntity<FolderDtoResponse> response = template.exchange(fullUrl(FOLDERS_URL),
                HttpMethod.POST, new HttpEntity<>(folderDtoRequest, login(email1)), FolderDtoResponse.class);
        FolderDtoResponse folderDtoResponse = response.getBody();
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(folderDtoResponse)
        );
        Folder folder = folderRepository.findById(folderDtoResponse.getId()).orElseThrow();
        assertAll(
                () -> assertEquals(folderDtoRequest.getName(), folder.getName()),
                () -> assertEquals(folderDtoRequest.getDescription(), folder.getDescription()),
                () -> assertEquals(userId, folder.getUserId())
        );
    }

    @Test
    public void addNullName() {
        checkBadRequestFieldError(() -> template.exchange(fullUrl(FOLDERS_URL), HttpMethod.POST,
                        new HttpEntity<>(new FolderDtoRequest(), login(email1)), FolderDtoResponse.class),
                "NotEmpty", "name");
    }

    @Test
    public void addEmptyName() {
        checkBadRequestFieldError(() -> template.exchange(fullUrl(FOLDERS_URL), HttpMethod.POST,
                        new HttpEntity<>(new FolderDtoRequest().setName(""), login(email1)), FolderDtoResponse.class),
                "NotEmpty", "name");
    }

    @Test
    public void addLongName() {
        FolderDtoRequest folderDtoRequest =
                createFolderDtoRequest(applicationProperties.getMaxNameLength() + 1);
        checkBadRequestFieldError(() -> template.exchange(fullUrl(FOLDERS_URL), HttpMethod.POST,
                        new HttpEntity<>(folderDtoRequest, login(email1)), FolderDtoResponse.class),
                "MaxLength", "name");
    }

    @Test
    public void addToManyFolders() {
        createFoldersInDB(FolderService.MAX_USER_FOLDERS_NUMBER);
        checkConflictError(() -> template.exchange(fullUrl(FOLDERS_URL), HttpMethod.POST,
                        new HttpEntity<>(createFolderDtoRequest(), login(email1)), FolderDtoResponse.class),
                "TOO_MANY_FOLDERS_CREATED");
    }

    @Test
    public void updateOk() {
        Folder folder = createFoldersInDB(1).values().stream().findAny().orElseThrow();
        int folderId = folder.getId();
        FolderDtoRequest folderDtoRequest = createFolderDtoRequest();
        ResponseEntity<Void> response = template.exchange(fullUrl(FOLDERS_URL + "/" + folderId),
                HttpMethod.PUT, new HttpEntity<>(folderDtoRequest, login(email1)), Void.class);
        Folder updatedFolder = folderRepository.findById(folderId).orElseThrow();
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(folderDtoRequest.getName(), updatedFolder.getName()),
                () -> assertEquals(folderDtoRequest.getDescription(), updatedFolder.getDescription())
        );
    }

    @Test
    public void updateNotFound() {
        checkNotFoundError(() -> template.exchange(fullUrl(FOLDERS_URL + "/1"),
                HttpMethod.PUT, new HttpEntity<>(createFolderDtoRequest(), login(email1)), Void.class));
    }

    @Test
    public void deleteOk() {
        Folder folder = createFoldersInDB(1).values().stream().findAny().orElseThrow();
        int folderId = folder.getId();
        ResponseEntity<Void> response = template.exchange(fullUrl(FOLDERS_URL + "/" + folderId),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), Void.class);
        Folder deletedFolder = folderRepository.findById(folderId).orElse(null);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNull(deletedFolder)
        );
    }

    @Test
    public void deleteWithSignalsOk() throws IOException {
        Pair<Integer, List<Integer>> folderAndSignalIds = createFolderWithSignalsInDB();
        int folderId = folderAndSignalIds.getFirst();
        String url = fullUrl(FOLDERS_URL + "/" + folderId + "?deleteSignals=true");
        ResponseEntity<Void> response = template.exchange(url, HttpMethod.DELETE,
                new HttpEntity<>(login(email1)), Void.class);
        Folder deletedFolder = folderRepository.findById(folderId).orElse(null);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNull(deletedFolder)
        );
        folderAndSignalIds.getSecond().forEach(signalId -> {
            Signal deletedSignal = signalRepository.findById(signalId).orElse(null);
            byte[] deletedWav = readWavFromFile(signalId);
            assertAll(
                    () -> assertNull(deletedSignal),
                    () -> assertEquals(0, deletedWav.length)
            );
        });
    }

    @Test
    public void deleteOnlyFolderOk() throws IOException {
        Pair<Integer, List<Integer>> folderAndSignalIds = createFolderWithSignalsInDB();
        int folderId = folderAndSignalIds.getFirst();
        String url = fullUrl(FOLDERS_URL + "/" + folderId + "?deleteSignals=false");
        ResponseEntity<Void> response = template.exchange(url, HttpMethod.DELETE,
                new HttpEntity<>(login(email1)), Void.class);
        Folder deletedFolder = folderRepository.findById(folderId).orElse(null);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNull(deletedFolder)
        );
        folderAndSignalIds.getSecond().forEach(signalId -> {
            Signal existingSignal = signalRepository.findById(signalId).orElse(null);
            byte[] existingWav = readWavFromFile(signalId);
            assertAll(
                    () -> assertNotNull(existingSignal),
                    () -> assertTrue(existingWav.length > 0)
            );
        });
    }

    @Test
    public void deleteNotFound() {
        checkNotFoundError(() -> template.exchange(fullUrl(FOLDERS_URL + "/1"),
                HttpMethod.DELETE, new HttpEntity<>(createFolderDtoRequest(), login(email1)), Void.class));
    }

    private byte[] readWavFromFile(int signalId) {
        try {
            return fileManager.readWavFromFile(userId, signalId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Integer, Folder> createFoldersInDB(int number) {
        Map<Integer, Folder> folders = new HashMap<>();
        for (int i = 1; i <= number; i++) {
            Folder folder = folderRepository.save(new Folder()
                    .setName("Folder" + i)
                    .setDescription("description" + i)
                    .setUserId(userId));
            folders.put(folder.getId(), folder);
        }
        return folders;
    }

    private Pair<Integer, List<Integer>> createFolderWithSignalsInDB() throws IOException {
        Set<Folder> folderSet = new HashSet<>(createFoldersInDB(1).values());
        List<Signal> signals = Stream.of(3000, 8000).map(sampleRate -> signalRepository.save(new Signal()
                .setName("Name " + sampleRate)
                .setDescription("description " + sampleRate)
                .setSampleRate(BigDecimal.valueOf(sampleRate))
                .setXMin(BigDecimal.ZERO)
                .setMaxAbsY(BigDecimal.ONE)
                .setUserId(userId)
                .setFolders(folderSet))).toList();
        for (Signal signal : signals) {
            fileManager.writeWavToFile(userId, signal.getId(),
                    SignalsTestUtils.generateWav(1000, 1, signal.getSampleRate().floatValue()));
        }
        return Pair.of(folderSet.stream().findAny().map(Folder::getId).orElseThrow(),
                signals.stream().map(Signal::getId).toList());
    }

    private FolderDtoRequest createFolderDtoRequest() {
        return createFolderDtoRequest(applicationProperties.getMaxNameLength() / 10);
    }

    private FolderDtoRequest createFolderDtoRequest(int nameLength) {
        return new FolderDtoRequest()
                .setName(RandomStringUtils.randomAlphanumeric(nameLength))
                .setDescription("description");
    }
}
