package link.signalapp.endpoint;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import link.signalapp.dto.request.SignalFilterDto;
import link.signalapp.dto.response.IdDtoResponse;
import link.signalapp.dto.request.SignalDtoRequest;
import link.signalapp.dto.response.ResponseWithTotalCounts;
import link.signalapp.dto.response.SignalDtoResponse;
import link.signalapp.dto.response.SignalWithDataDtoResponse;
import link.signalapp.error.SignalAppConflictException;
import link.signalapp.error.SignalAppException;
import link.signalapp.error.SignalAppNotFoundException;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.service.SignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.validation.Valid;

/**
 * @author anton
 */
@RestController
@RequestMapping("/api/signals")
@RequiredArgsConstructor
public class SignalEndpoint extends EndpointBase {

    private final SignalService signalService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseWithTotalCounts<SignalDtoResponse> get(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "0") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) throws SignalAppUnauthorizedException {
        return signalService.filter(sessionId, new SignalFilterDto()
                .setSearch(search)
                .setPage(page)
                .setSize(size)
                .setSortBy(sortBy)
                .setSortDir(sortDir));
    }

    @PostMapping(path = "/filter", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseWithTotalCounts<SignalDtoResponse> filter(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @RequestBody SignalFilterDto filter
    ) throws SignalAppUnauthorizedException {
        return signalService.filter(sessionId, filter);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    IdDtoResponse post(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @RequestBody @Valid SignalDtoRequest signalDtoRequest
    ) throws SignalAppUnauthorizedException, IOException, SignalAppConflictException {
        return signalService.add(sessionId, signalDtoRequest);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void put(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @RequestBody @Valid SignalDtoRequest signalDtoRequest, @PathVariable int id
    ) throws SignalAppUnauthorizedException, IOException, SignalAppNotFoundException {
        signalService.update(sessionId, signalDtoRequest, id);
    }

    @DeleteMapping("/{id}")
    void delete(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        signalService.delete(sessionId, id);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    SignalWithDataDtoResponse getSignalWithData(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, SignalAppNotFoundException, UnsupportedAudioFileException, IOException {
        return signalService.getSignalWithData(sessionId, id);
    }

    @GetMapping(path = "/{id}/data", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BigDecimal> getData(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, SignalAppNotFoundException, UnsupportedAudioFileException, IOException {
        return signalService.getData(sessionId, id);
    }

    @GetMapping(path = "/{id}/wav", produces = "audio/wave")
    byte[] getWav(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, SignalAppNotFoundException, IOException {
        return signalService.getWav(sessionId, id);
    }

    @PostMapping(path = "/wav/{fileName}", consumes = "audio/wave")
    void postWav(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @PathVariable String fileName, @RequestBody byte[] data
    ) throws UnsupportedAudioFileException, SignalAppUnauthorizedException, IOException, SignalAppException {
        signalService.importWav(sessionId, fileName, data);
    }

    @GetMapping(path = "/sample-rates", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BigDecimal> getSampleRates(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId
    ) throws SignalAppUnauthorizedException {
        return signalService.getSampleRates(sessionId);
    }

}
