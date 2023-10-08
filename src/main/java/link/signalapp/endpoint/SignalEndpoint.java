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
import jakarta.validation.Valid;

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
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "0") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) throws SignalAppUnauthorizedException {
        return signalService.filter(new SignalFilterDto()
                .setSearch(search)
                .setPage(page)
                .setSize(size)
                .setSortBy(sortBy)
                .setSortDir(sortDir));
    }

    @PostMapping(path = "/filter", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseWithTotalCounts<SignalDtoResponse> filter(
            @RequestBody SignalFilterDto filter
    ) throws SignalAppUnauthorizedException {
        return signalService.filter(filter);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    IdDtoResponse post(
            @RequestBody @Valid SignalDtoRequest signalDtoRequest
    ) throws SignalAppUnauthorizedException, IOException, SignalAppConflictException {
        return signalService.add(signalDtoRequest);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void put(
            @RequestBody @Valid SignalDtoRequest signalDtoRequest,
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, IOException, SignalAppNotFoundException {
        signalService.update(signalDtoRequest, id);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        signalService.delete(id);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    SignalWithDataDtoResponse getSignalWithData(
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, SignalAppNotFoundException, UnsupportedAudioFileException, IOException {
        return signalService.getSignalWithData(id);
    }

    @GetMapping(path = "/{id}/data", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BigDecimal> getData(
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, SignalAppNotFoundException, UnsupportedAudioFileException, IOException {
        return signalService.getData(id);
    }

    @GetMapping(path = "/{id}/wav", produces = "audio/wave")
    byte[] getWav(
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, SignalAppNotFoundException, IOException {
        return signalService.getWav(id);
    }

    @PostMapping(path = "/wav/{fileName}", consumes = "audio/wave")
    void postWav(
            @PathVariable String fileName,
            @RequestBody byte[] data
    ) throws UnsupportedAudioFileException, SignalAppUnauthorizedException, IOException, SignalAppException {
        signalService.importWav(fileName, data);
    }

    @GetMapping(path = "/sample-rates", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BigDecimal> getSampleRates() throws SignalAppUnauthorizedException {
        return signalService.getSampleRates();
    }

}
