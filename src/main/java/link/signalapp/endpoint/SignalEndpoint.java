package link.signalapp.endpoint;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import link.signalapp.dto.request.SignalDtoRequest;
import link.signalapp.dto.request.SignalsPageDtoRequest;
import link.signalapp.dto.response.IdDtoResponse;
import link.signalapp.dto.response.PageDtoResponse;
import link.signalapp.dto.response.SignalDtoResponse;
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
    public PageDtoResponse<SignalDtoResponse> get(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "0") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir
    ) {
        return signalService.getPage(new SignalsPageDtoRequest()
                .setSearch(search)
                .setPage(page)
                .setSize(size)
                .setSortBy(sortBy)
                .setSortDir(sortDir));
    }

    @PostMapping(path = "/filter", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public PageDtoResponse<SignalDtoResponse> getPage(@RequestBody @Valid SignalsPageDtoRequest request) {
        return signalService.getPage(request);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IdDtoResponse post(
            @RequestPart @Valid SignalDtoRequest json,
            @RequestPart byte[] data
    ) throws IOException, UnsupportedAudioFileException {
        return signalService.add(json, data);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void put(
            @RequestPart @Valid SignalDtoRequest json,
            @RequestPart byte[] data,
            @PathVariable int id
    ) throws IOException, UnsupportedAudioFileException {
        signalService.update(json, data, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        signalService.delete(id);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SignalDtoResponse getSignal(@PathVariable int id) {
        return signalService.getSignal(id);
    }

    @GetMapping(path = "/{id}/wav", produces = "audio/wave")
    public byte[] getWav(@PathVariable int id) throws IOException {
        return signalService.getWav(id);
    }

    @GetMapping(path = "/sample-rates", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BigDecimal> getSampleRates() {
        return signalService.getSampleRates();
    }

}
