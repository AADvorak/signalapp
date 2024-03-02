package link.signalapp;

import link.signalapp.dto.request.SignalDtoRequest;
import link.signalapp.dto.response.IdDtoResponse;
import link.signalapp.file.FileManager;
import link.signalapp.model.Signal;
import link.signalapp.repository.SignalRepository;
import link.signalapp.service.SignalService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoadSignalsIntegrationTest extends IntegrationTestBase {

    private static final String SIGNALS_URL = "/api/signals";
    private static final int SMALL_WAV_LENGTH = 1000;
    private static final int AVAILABLE_CHANNELS_NUMBER = 1;

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
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
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
    public void uploadSignalNullData() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest(), null);
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "MISSING_REQUEST_PART", "data");
    }

    @Test
    public void uploadSignalEmptyData() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(
                createSignalDtoRequest(), new byte[0]);
        checkBadRequestFieldError(
                () -> template.exchange(fullUrl(SIGNALS_URL), HttpMethod.POST, entity, IdDtoResponse.class),
                "MISSING_REQUEST_PART", "data");
    }

    @Test
    public void uploadSignalTooLong() throws IOException {
        HttpEntity<MultiValueMap<String, Object>> entity = createHttpEntity(createSignalDtoRequest(),
                generateWav(SignalService.MAX_SIGNAL_LENGTH + 1, AVAILABLE_CHANNELS_NUMBER));
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
        return generateWav(SMALL_WAV_LENGTH, AVAILABLE_CHANNELS_NUMBER);
    }

    private byte[] generateWav(int length, int channels) throws IOException {
        final double sampleRate = 8000.0;
        float[] buffer = new float[length];
        for (int sample = 0; sample < buffer.length; sample++) {
            buffer[sample] = new Random().nextFloat();
        }
        final byte[] byteBuffer = new byte[buffer.length * 2];
        int bufferIndex = 0;
        for (int i = 0; i < byteBuffer.length; i++) {
            final int x = (int)(buffer[bufferIndex++] * 32767.0);
            byteBuffer[i++] = (byte)x;
            byteBuffer[i] = (byte)(x >>> 8);
        }
        final boolean bigEndian = false;
        final boolean signed = true;
        final int bits = 16;
        AudioFormat format = new AudioFormat((float)sampleRate, bits, channels, signed, bigEndian);
        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, buffer.length);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, baos);
        return baos.toByteArray();
    }

    private HttpEntity<MultiValueMap<String, Object>> createHttpEntity(SignalDtoRequest signalDtoRequest, byte[] testWav) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("json", signalDtoRequest);
        body.add("data", testWav);
        return new HttpEntity<>(body, createHeaders());
    }
}
