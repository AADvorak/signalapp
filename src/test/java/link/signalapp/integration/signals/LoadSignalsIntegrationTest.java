package link.signalapp.integration.signals;

import link.signalapp.dto.request.SignalDtoRequest;
import link.signalapp.dto.response.IdDtoResponse;
import link.signalapp.dto.response.SignalDtoResponse;
import link.signalapp.file.FileManager;
import link.signalapp.integration.IntegrationTestBase;
import link.signalapp.model.Role;
import link.signalapp.model.Signal;
import link.signalapp.repository.SignalRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoadSignalsIntegrationTest extends IntegrationTestBase {

    private static final String SIGNALS_URL = "/api/signals";
    private static final int SMALL_WAV_LENGTH = 1000;
    private static final int AVAILABLE_CHANNELS_NUMBER = 1;
    private static final float SAMPLE_RATE = 8000.0F;

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private FileManager fileManager;

    private int userId;
    private int extendedStorageUserId;

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
        registerUsers();
        giveRoleToUser(email2, getRoleByName(Role.EXTENDED_STORAGE));
        userId = userRepository.findByEmail(email1).getId();
        extendedStorageUserId = userRepository.findByEmail(email2).getId();
    }

    @AfterAll
    public void afterAll() {
        fileManager.deleteDirRecursively(new File(applicationProperties.getDataPath() + "signals"));
    }

    @BeforeEach
    public void clearSignals() {
        signalRepository.deleteAll();
        fileManager.deleteAllUserData(userId);
    }

    @Test
    public void uploadSignalOk() throws IOException {
        SignalDtoRequest signalDtoRequest = createSignalDtoRequest();
        byte[] testWav = getTestWav();
        ResponseEntity<IdDtoResponse> response = template.exchange(fullUrl(SIGNALS_URL),
                HttpMethod.POST, createHttpEntity(signalDtoRequest, testWav), IdDtoResponse.class);
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody())
        );
        int signalId = response.getBody().getId();
        Signal signal = signalRepository.findByIdAndUserId(signalId, userId).orElseThrow();
        byte[] writtenWav = fileManager.readWavFromFile(userId, signalId);
        assertAll(
                () -> assertEquals(signalDtoRequest.getName(), signal.getName()),
                () -> assertEquals(signalDtoRequest.getDescription(), signal.getDescription()),
                () -> assertEquals(signalDtoRequest.getSampleRate(), signal.getSampleRate()),
                () -> assertEquals(signalDtoRequest.getMaxAbsY(), signal.getMaxAbsY()),
                () -> assertEquals(signalDtoRequest.getXMin(), signal.getXMin()),
                () -> assertArrayEquals(testWav, writtenWav)
        );
    }

    @Test
    public void uploadSignalNullName() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setName(null), getTestWav());
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "NotEmpty", "name");
    }

    @Test
    public void uploadSignalEmptyName() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setName(""), getTestWav());
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "NotEmpty", "name");
    }

    @Test
    public void uploadSignalNullMaxAbsY() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setMaxAbsY(null), getTestWav());
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "NotNull", "maxAbsY");
    }

    @Test
    public void uploadSignalZeroMaxAbsY() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setMaxAbsY(BigDecimal.ZERO), getTestWav());
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "Positive", "maxAbsY");
    }

    @Test
    public void uploadSignalNullXMin() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setXMin(null), getTestWav());
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "NotNull", "xMin");
    }

    @Test
    public void uploadSignalNullSampleRate() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setSampleRate(null), getTestWav());
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "NotNull", "sampleRate");
    }

    @Test
    public void uploadSignalEmptySampleRate() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setSampleRate(BigDecimal.ZERO), getTestWav());
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "Positive", "sampleRate");
    }

    @Test
    public void uploadSignalNullJson() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                null, getTestWav());
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "MISSING_REQUEST_PART", "json");
    }

    @Test
    public void uploadSignalNullData() {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest(), null);
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "MISSING_REQUEST_PART", "data");
    }

    @Test
    public void uploadSignalEmptyData() {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest(), new byte[0]);
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "MISSING_REQUEST_PART", "data");
    }

    @Test
    public void uploadSignalTooLong() throws IOException {
        int maxSignalLength = applicationProperties.getLimits().getMaxSignalLength();
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(createSignalDtoRequest(),
                generateWav(maxSignalLength + 1, AVAILABLE_CHANNELS_NUMBER));
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "TOO_LONG_SIGNAL");
    }

    @Test
    public void uploadSignalMoreThenOneChannel() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(createSignalDtoRequest(),
                generateWav(SMALL_WAV_LENGTH, 2));
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "WRONG_VAW_FORMAT");
    }

    @Test
    public void uploadSignalMaxUserStoredNumber() throws IOException {
        int maxUserSignalsNumber = applicationProperties.getLimits().getMaxUserSignalsNumber();
        for (int i = 0; i < maxUserSignalsNumber; i++) {
            signalRepository.save(createRandomSignal(userId));
        }
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(createSignalDtoRequest(), getTestWav());
        checkConflictError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "TOO_MANY_SIGNALS_STORED");
    }

    @Test
    public void uploadSignalWithExtendedStorageOk() throws IOException {
        int maxUserSignalsNumber = applicationProperties.getLimits().getMaxUserSignalsNumber();
        for (int i = 0; i < maxUserSignalsNumber; i++) {
            signalRepository.save(createRandomSignal(extendedStorageUserId));
        }
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(email2,
                createSignalDtoRequest(), getTestWav());
        ResponseEntity<IdDtoResponse> response = template.exchange(fullUrl(SIGNALS_URL),
                HttpMethod.POST, entity, IdDtoResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void uploadSignalWithExtendedStorageMaxUserStoredNumber() throws IOException {
        int maxUserSignalsNumber = applicationProperties.getLimits().getMaxUserSignalsNumber()
                * applicationProperties.getLimits().getExtendedStorageMultiplier();
        for (int i = 0; i < maxUserSignalsNumber; i++) {
            signalRepository.save(createRandomSignal(extendedStorageUserId));
        }
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(email2,
                createSignalDtoRequest(), getTestWav());
        checkConflictError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "TOO_MANY_SIGNALS_STORED");
    }

    @Test
    public void updateSignalOk() throws IOException {
        SignalDtoRequest signalDtoRequest = createSignalDtoRequest();
        ResponseEntity<IdDtoResponse> response = template.exchange(fullUrl(SIGNALS_URL),
                HttpMethod.POST, createHttpEntity(signalDtoRequest, getTestWav()), IdDtoResponse.class);
        int signalId = Objects.requireNonNull(response.getBody()).getId();
        signalDtoRequest
                .setName(signalDtoRequest.getName() + " updated")
                .setDescription(signalDtoRequest.getDescription() + " updated")
                .setMaxAbsY(BigDecimal.TEN)
                .setXMin(BigDecimal.ONE);
        byte[] updateWav = getTestWav();
        ResponseEntity<String> updateResponse = template.exchange(fullUrl(SIGNALS_URL + "/" + signalId),
                HttpMethod.PUT, createHttpEntity(signalDtoRequest, updateWav), String.class);
        Signal signal = signalRepository.findByIdAndUserId(signalId, userId).orElseThrow();
        byte[] writtenWav = fileManager.readWavFromFile(userId, signalId);
        assertAll(
                () -> assertEquals(HttpStatus.OK, updateResponse.getStatusCode()),
                () -> assertEquals(signalDtoRequest.getName(), signal.getName()),
                () -> assertEquals(signalDtoRequest.getDescription(), signal.getDescription()),
                () -> assertEquals(signalDtoRequest.getSampleRate(), signal.getSampleRate()),
                () -> assertEquals(signalDtoRequest.getMaxAbsY(), signal.getMaxAbsY()),
                () -> assertEquals(signalDtoRequest.getXMin(), signal.getXMin()),
                () -> assertArrayEquals(updateWav, writtenWav)
        );
    }

    @Test
    public void updateSignalNotFound() {
        checkNotFoundError(() -> template.exchange(fullUrl(SIGNALS_URL + "/1"),
                HttpMethod.PUT, createHttpEntity(createSignalDtoRequest(), getTestWav()), String.class));
    }

    @Test
    public void deleteSignalOk() throws IOException {
        SignalDtoRequest signalDtoRequest = createSignalDtoRequest();
        ResponseEntity<IdDtoResponse> response = template.exchange(fullUrl(SIGNALS_URL),
                HttpMethod.POST, createHttpEntity(signalDtoRequest, getTestWav()), IdDtoResponse.class);
        int signalId = Objects.requireNonNull(response.getBody()).getId();
        ResponseEntity<Void> deleteResponse = template.exchange(fullUrl(SIGNALS_URL + "/" + signalId),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), Void.class);
        Signal deletedSignal = signalRepository.findById(signalId).orElse(null);
        byte[] deletedWav = readWavFromFile(signalId);
        assertAll(
                () -> assertEquals(HttpStatus.OK, deleteResponse.getStatusCode()),
                () -> assertNull(deletedSignal),
                () -> assertEquals(0, deletedWav.length)
        );
    }

    @Test
    public void deleteSignalNotFound() {
        checkNotFoundError(() -> template.exchange(fullUrl(SIGNALS_URL + "/1"),
                HttpMethod.DELETE, new HttpEntity<>(login(email1)), Void.class));
    }

    @Test
    public void getSignalOk() {
        Signal signal = signalRepository.save(createRandomSignal(userId));
        HttpHeaders headers = login(email1);
        ResponseEntity<SignalDtoResponse> signalResponse = template.exchange(
                fullUrl(SIGNALS_URL + "/" + signal.getId()), HttpMethod.GET,
                new HttpEntity<>(headers), SignalDtoResponse.class);
        SignalDtoResponse signalDtoResponse = signalResponse.getBody();
        assertAll(
                () -> assertEquals(HttpStatus.OK, signalResponse.getStatusCode()),
                () -> assertNotNull(signalDtoResponse)
        );
        assertAll(
                () -> assertEquals(signal.getName(), signalDtoResponse.getName()),
                () -> assertEquals(signal.getDescription(), signalDtoResponse.getDescription()),
                () -> assertEquals(signal.getSampleRate(), signalDtoResponse.getSampleRate()),
                () -> assertEquals(signal.getMaxAbsY(), signalDtoResponse.getMaxAbsY()),
                () -> assertEquals(signal.getXMin(), signalDtoResponse.getXMin())
        );
    }

    @Test
    public void getWavOk() throws IOException {
        SignalDtoRequest signalDtoRequest = createSignalDtoRequest();
        byte[] testWav = getTestWav();
        ResponseEntity<IdDtoResponse> response = template.exchange(fullUrl(SIGNALS_URL),
                HttpMethod.POST, createHttpEntity(signalDtoRequest, testWav), IdDtoResponse.class);
        int signalId = Objects.requireNonNull(response.getBody()).getId();
        HttpHeaders headers = login(email1);
        ResponseEntity<byte[]> wavResponse = template.exchange(
                fullUrl(SIGNALS_URL + "/" + signalId + "/wav"), HttpMethod.GET,
                new HttpEntity<>(headers), byte[].class);
        byte[] wavResponseBody = wavResponse.getBody();
        assertAll(
                () -> assertEquals(HttpStatus.OK, wavResponse.getStatusCode()),
                () -> assertArrayEquals(testWav, wavResponseBody)
        );
    }

    @Test
    public void getSignalNotFound() {
        checkNotFoundError(() -> template.exchange(fullUrl(SIGNALS_URL + "/1"), HttpMethod.GET,
                new HttpEntity<>(login(email1)), SignalDtoResponse.class));
    }

    @Test
    public void getWavNotFound() {
        checkNotFoundError(() -> template.exchange(fullUrl(SIGNALS_URL + "/1/wav"), HttpMethod.GET,
                new HttpEntity<>(login(email1)), byte[].class));
    }

    private byte[] readWavFromFile(int signalId) {
        try {
            return fileManager.readWavFromFile(userId, signalId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SignalDtoRequest createSignalDtoRequest() {
        return new SignalDtoRequest()
                .setName("Name")
                .setDescription("Description")
                .setSampleRate(BigDecimal.valueOf(SAMPLE_RATE))
                .setMaxAbsY(BigDecimal.ONE)
                .setXMin(BigDecimal.ZERO);
    }

    private Signal createRandomSignal(int userId) {
        return SignalsTestUtils.createRandomSignal()
                .setUserId(userId)
                .setSampleRate(BigDecimal.valueOf(SAMPLE_RATE));
    }

    private byte[] getTestWav() throws IOException {
        return generateWav(SMALL_WAV_LENGTH, AVAILABLE_CHANNELS_NUMBER);
    }

    private byte[] generateWav(int length, int channels) throws IOException {
        return SignalsTestUtils.generateWav(length, channels, SAMPLE_RATE);
    }

    private HttpEntity<MultiValueMap<String, Object>> createHttpEntity(
            SignalDtoRequest signalDtoRequest, byte[] testWav) {
        return createHttpEntity(email1, signalDtoRequest, testWav);
    }

    private HttpEntity<MultiValueMap<String, Object>> createHttpEntity(
            String email, SignalDtoRequest signalDtoRequest, byte[] testWav) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("json", signalDtoRequest);
        body.add("data", testWav);
        HttpHeaders headers = login(email);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(body, headers);
    }
}
