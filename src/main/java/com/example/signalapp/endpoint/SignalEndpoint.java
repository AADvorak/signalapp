package com.example.signalapp.endpoint;

import java.io.IOException;
import java.util.List;

import com.example.signalapp.dto.response.IdDtoResponse;
import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.dto.request.SignalDtoRequest;
import com.example.signalapp.dto.response.ResponseWithTotalCounts;
import com.example.signalapp.dto.response.SignalDtoResponse;
import com.example.signalapp.dto.response.SignalWithDataDtoResponse;
import com.example.signalapp.error.SignalAppConflictException;
import com.example.signalapp.error.SignalAppException;
import com.example.signalapp.error.SignalAppNotFoundException;
import com.example.signalapp.error.SignalAppUnauthorizedException;
import com.example.signalapp.service.SignalService;
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

    private final SignalService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseWithTotalCounts<SignalDtoResponse> get(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                                   @RequestParam(required = false) String filter,
                                                   @RequestParam(required = false, defaultValue = "0") int page,
                                                   @RequestParam(required = false, defaultValue = "0") int size,
                                                   @RequestParam(required = false) String sortBy,
                                                   @RequestParam(required = false) String sortDir)
            throws SignalAppUnauthorizedException {
        return service.get(sessionId, filter, page, size, sortBy, sortDir);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    IdDtoResponse post(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                       @RequestBody @Valid SignalDtoRequest signalDtoRequest) throws SignalAppUnauthorizedException,
            IOException, SignalAppConflictException {
        return service.add(sessionId, signalDtoRequest);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void put(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
             @RequestBody @Valid SignalDtoRequest signalDtoRequest, @PathVariable int id)
            throws SignalAppUnauthorizedException, IOException, SignalAppNotFoundException {
        service.update(sessionId, signalDtoRequest, id);
    }

    @DeleteMapping("/{id}")
    void delete(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                @PathVariable int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        service.delete(sessionId, id);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    SignalWithDataDtoResponse getSignalWithData(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                                @PathVariable int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException,
            UnsupportedAudioFileException, IOException {
        return service.getSignalWithData(sessionId, id);
    }

    @GetMapping(path = "/{id}/data", produces = MediaType.APPLICATION_JSON_VALUE)
    List<SignalDataDto> getData(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                @PathVariable int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException,
            UnsupportedAudioFileException, IOException {
        return service.getData(sessionId, id);
    }

    @GetMapping(path = "/{id}/wav", produces = "audio/wave")
    byte[] getWav(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                  @PathVariable int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException, IOException {
        return service.getWav(sessionId, id);
    }

    @PostMapping(path = "/wav/{fileName}", consumes = "audio/wave")
    void postWav(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                 @PathVariable String fileName, @RequestBody byte[] data)
            throws UnsupportedAudioFileException, SignalAppUnauthorizedException, IOException, SignalAppException {
        service.importWav(sessionId, fileName, data);
    }

}
