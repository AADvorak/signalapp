package link.signalapp;

import link.signalapp.dto.request.SignalDtoRequest;
import link.signalapp.dto.response.IdDtoResponse;
import link.signalapp.file.FileManager;
import link.signalapp.model.Signal;
import link.signalapp.repository.SignalRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoadSignalsIntegrationTest extends IntegrationTestBase {

    private static final String SIGNALS_URL = "/api/signals";

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private FileManager fileManager;

    @BeforeAll
    public void beforeAll() {
        userRepository.deleteAll();
        registerUsers();
    }

    @BeforeEach
    public void clearSignals() {
        signalRepository.deleteAll();
        int userId = userRepository.findByEmail(email1).getId();
        fileManager.deleteAllUserData(userId);
    }

    @Test
    public void uploadSignalOk() throws IOException {
        SignalDtoRequest signalDtoRequest = createSignalDtoRequest();
        byte[] testWav = getTestWav();
        ResponseEntity<IdDtoResponse> response = template.exchange(fullUrl(SIGNALS_URL),
                HttpMethod.POST, createHttpEntity(signalDtoRequest, testWav), IdDtoResponse.class);
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), response.getStatusCode().value()),
                () -> assertNotNull(response.getBody())
        );
        int signalId = response.getBody().getId();
        int userId = userRepository.findByEmail(email1).getId();
        Signal signal = signalRepository.findByIdAndUserId(signalId, userId);
        assertNotNull(signal);
        assertAll(
                () -> assertEquals(signalDtoRequest.getName(), signal.getName()),
                () -> assertEquals(signalDtoRequest.getDescription(), signal.getDescription()),
                () -> assertEquals(signalDtoRequest.getSampleRate(), signal.getSampleRate()),
                () -> assertEquals(signalDtoRequest.getMaxAbsY(), signal.getMaxAbsY()),
                () -> assertEquals(signalDtoRequest.getXMin(), signal.getXMin())
        );
        byte[] writtenWav = fileManager.readWavFromFile(userId, signalId);
        assertArrayEquals(testWav, writtenWav);
    }

    @Test
    public void uploadSignalNullName() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setName(null), getTestWav());
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "NotEmpty", "name");
    }

    @Test
    public void uploadSignalEmptyName() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setName(""), getTestWav());
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "NotEmpty", "name");
    }

    @Test
    public void uploadSignalNullMaxAbsY() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setMaxAbsY(null), getTestWav());
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "NotNull", "maxAbsY");
    }

    @Test
    public void uploadSignalZeroMaxAbsY() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setMaxAbsY(BigDecimal.ZERO), getTestWav());
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "Positive", "maxAbsY");
    }

    @Test
    public void uploadSignalNullXMin() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setXMin(null), getTestWav());
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "NotNull", "xMin");
    }

    @Test
    public void uploadSignalNullSampleRate() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setSampleRate(null), getTestWav());
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "NotNull", "sampleRate");
    }

    @Test
    public void uploadSignalEmptySampleRate() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest().setSampleRate(BigDecimal.ZERO), getTestWav());
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "Positive", "sampleRate");
    }

    @Test
    public void uploadSignalNullJson() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                null, getTestWav());
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "MISSING_REQUEST_PART", "json");
    }

    @Test
    public void uploadSignalNullData() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest(), null);
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "MISSING_REQUEST_PART", "data");
    }

    @Test
    public void uploadSignalEmptyData() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest(), new byte[0]);
        checkBadRequestError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "MISSING_REQUEST_PART", "data");
    }

    @Test
    public void uploadSignalTooLong() {

    }

    @Test
    public void uploadSignalMoreThenOneChannel() {

    }

    @Test
    public void uploadSignalMaxUserStoredNumber() {

    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = login(email1);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private SignalDtoRequest createSignalDtoRequest() {
        return new SignalDtoRequest()
                .setName("Name")
                .setDescription("Description")
                .setSampleRate(BigDecimal.valueOf(8000))
                .setMaxAbsY(BigDecimal.ONE)
                .setXMin(BigDecimal.ZERO);
    }

    private byte[] getTestWav() throws IOException {
        String path = "src/test/resources/wav/air.wav";
        File file = new File(path);
        return Files.readAllBytes(file.toPath());
    }

    private HttpEntity<MultiValueMap<String, Object>> createHttpEntity(SignalDtoRequest signalDtoRequest, byte[] testWav) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("json", signalDtoRequest);
        body.add("data", testWav);
        return new HttpEntity<>(body, createHeaders());
    }
}
