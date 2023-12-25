package link.signalapp.endpoint;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import link.signalapp.dto.request.SignalDtoRequest;
import link.signalapp.dto.request.SignalFilterDto;
import link.signalapp.dto.response.IdDtoResponse;
import link.signalapp.dto.response.ResponseWithTotalCounts;
import link.signalapp.dto.response.SignalDtoResponse;
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

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    IdDtoResponse post(
            @RequestPart @Valid SignalDtoRequest json,
            @RequestPart byte[] data
    ) throws SignalAppUnauthorizedException, IOException, SignalAppException, UnsupportedAudioFileException {
        return signalService.add(json, data);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void put(
            @RequestPart @Valid SignalDtoRequest json,
            @RequestPart byte[] data,
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, IOException, SignalAppNotFoundException,
            UnsupportedAudioFileException, SignalAppException {
        signalService.update(json, data, id);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        signalService.delete(id);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    SignalDtoResponse getSignal(
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        return signalService.getSignal(id);
    }

    @GetMapping(path = "/{id}/wav", produces = "audio/wave")
    byte[] getWav(
            @PathVariable int id
    ) throws SignalAppUnauthorizedException, SignalAppNotFoundException, IOException {
        return signalService.getWav(id);
    }

    @GetMapping(path = "/sample-rates", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BigDecimal> getSampleRates() throws SignalAppUnauthorizedException {
        return signalService.getSampleRates();
    }

}
