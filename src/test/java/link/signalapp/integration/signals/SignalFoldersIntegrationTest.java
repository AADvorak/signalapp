package link.signalapp.integration.signals;

import link.signalapp.integration.IntegrationTestBase;
import link.signalapp.model.Folder;
import link.signalapp.model.Signal;
import link.signalapp.repository.FolderRepository;
import link.signalapp.repository.SignalRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignalFoldersIntegrationTest extends IntegrationTestBase {

    private static final String SIGNAL_FOLDERS_URL = "/api/signals/{id}/folders";

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private FolderRepository folderRepository;

    private Map<Integer, Folder> folderMap;

    private Map<Integer, Signal> signalMap;

    private int userId;

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
        registerUsers();
        userId = userRepository.findByEmail(email1).getId();
    }

    @BeforeEach
    public void beforeEach() {
        signalRepository.deleteAll();
        folderRepository.deleteAll();
        createFoldersInDB();
        createSignalsInDB();
    }

    @Test
    public void getFolderIdsOk() {
        for (int i = 0; i <= 2; i++) {
            ResponseEntity<Integer[]> response = template.exchange(getUrl(signalMap.get(i).getId()),
                    HttpMethod.GET, new HttpEntity<>(login(email1)), Integer[].class);
            Set<Integer> folderIds = Arrays.stream(Objects.requireNonNull(response.getBody()))
                    .collect(Collectors.toSet());
            int number = i;
            assertAll(
                    () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                    () -> assertEquals(getPartOfFoldersIds(number), folderIds)
            );
        }
    }

    @Test
    public void getFolderIdsNotFound() {
        checkNotFoundError(() -> template.exchange(getUrl(getNotExistingSignalId()),
                HttpMethod.GET, new HttpEntity<>(login(email1)), Integer[].class));
    }

    @Test
    public void addFolderOk() {
        int noFoldersSignalId = signalMap.get(0).getId();
        int folderId = getAnyFolderId();
        ResponseEntity<Void> response = template.exchange(getUrl(noFoldersSignalId, folderId),
                HttpMethod.POST, new HttpEntity<>(login(email1)), Void.class);
        Set<Integer> afterAddSignalFolderIds = getSignalFolderIdSet(noFoldersSignalId);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(1, afterAddSignalFolderIds.size()),
                () -> assertEquals(folderId, afterAddSignalFolderIds.stream().findAny().orElseThrow())
        );
    }

    @Test
    public void addFolderAlreadyAddedOk() {
        int allFoldersSignalId = signalMap.get(2).getId();
        int folderId = getAnyFolderId();
        ResponseEntity<Void> response = template.exchange(getUrl(allFoldersSignalId, folderId),
                HttpMethod.POST, new HttpEntity<>(login(email1)), Void.class);
        Set<Integer> afterAddSignalFolderIds = getSignalFolderIdSet(allFoldersSignalId);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(2, afterAddSignalFolderIds.size()),
                () -> assertEquals(folderMap.keySet(), afterAddSignalFolderIds)
        );
    }

    @Test
    public void addFolderNotFoundSignal() {
        checkNotFoundError(() -> template.exchange(getUrl(getNotExistingSignalId(), getAnyFolderId()),
                HttpMethod.POST, new HttpEntity<>(login(email1)), Void.class));
    }

    @Test
    public void addFolderNotFoundFolder() {
        checkNotFoundError(() -> template.exchange(getUrl(signalMap.get(0).getId(), getNotExistingFolderId()),
                HttpMethod.POST, new HttpEntity<>(login(email1)), Void.class));
    }

    @Test
    public void deleteFolderOk() {
        int allFoldersSignalId = signalMap.get(2).getId();
        int folderId = getAnyFolderId();
        ResponseEntity<Void> response = template.exchange(getUrl(allFoldersSignalId, folderId),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), Void.class);
        Set<Integer> afterDeleteSignalFolderIds = getSignalFolderIdSet(allFoldersSignalId);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(1, afterDeleteSignalFolderIds.size()),
                () -> assertFalse(afterDeleteSignalFolderIds.contains(folderId))
        );
    }

    @Test
    public void deleteFolderNotExistingOk() {
        int noFoldersSignalId = signalMap.get(0).getId();
        int folderId = getAnyFolderId();
        ResponseEntity<Void> response = template.exchange(getUrl(noFoldersSignalId, folderId),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), Void.class);
        Set<Integer> afterDeleteSignalFolderIds = getSignalFolderIdSet(noFoldersSignalId);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(0, afterDeleteSignalFolderIds.size())
        );
    }

    @Test
    public void deleteFolderNotFoundSignal() {
        checkNotFoundError(() -> template.exchange(getUrl(getNotExistingSignalId(), getAnyFolderId()),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), Void.class));
    }

    private void createFoldersInDB() {
        folderMap = new HashMap<>();
        for (int i = 1; i <= 2; i++) {
            Folder folder = folderRepository.save(new Folder()
                    .setName("Folder" + i)
                    .setDescription("description" + i)
                    .setUserId(userId));
            folderMap.put(folder.getId(), folder);
        }
    }

    private void createSignalsInDB() {
        signalMap = new HashMap<>();
        for (int i = 0; i <= 2; i++) {
            Signal signal = signalRepository.save(SignalsTestUtils.createRandomSignal()
                    .setUserId(userId)
                    .setSampleRate(BigDecimal.valueOf(8000))
                    .setFolders(getPartOfFolders(i)));
            signalMap.put(i, signal);
        }
    }

    private Set<Integer> getSignalFolderIdSet(int signalId) {
        ResponseEntity<Integer[]> folderIdsResponse = template.exchange(getUrl(signalId),
                HttpMethod.GET, new HttpEntity<>(login(email1)), Integer[].class);
        return Arrays.stream(Objects.requireNonNull(folderIdsResponse.getBody()))
                .collect(Collectors.toSet());
    }

    private Set<Integer> getPartOfFoldersIds(int number) {
        return getPartOfFoldersStream(number)
                .map(Folder::getId)
                .collect(Collectors.toSet());
    }

    private Set<Folder> getPartOfFolders(int number) {
        return getPartOfFoldersStream(number)
                .collect(Collectors.toSet());
    }

    private Stream<Folder> getPartOfFoldersStream(int number) {
        return folderMap.values()
                .stream()
                .limit(number);
    }

    private int getNotExistingSignalId() {
        return signalMap.values().stream()
                .map(Signal::getId)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
    }

    private int getNotExistingFolderId() {
        return folderMap.keySet().stream()
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
    }

    private int getAnyFolderId() {
        return folderMap.keySet().stream().findAny().orElseThrow();
    }

    private String getUrl(int id, int folderId) {
        return getUrl(id) + "/" + folderId;
    }

    private String getUrl(int id) {
        return fullUrl(SIGNAL_FOLDERS_URL.replace("{id}", String.valueOf(id)));
    }
}
