package com.example.signalapp.endpoint;

import java.io.IOException;
import java.util.List;

import com.example.signalapp.dto.response.IdDtoResponse;
import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.dto.request.SignalDtoRequest;
import com.example.signalapp.dto.response.SignalDtoResponse;
import com.example.signalapp.error.SignalAppDataException;
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
    List<SignalDtoResponse> getAll(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId)
            throws SignalAppUnauthorizedException {
        return service.getAll(sessionId);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    IdDtoResponse post(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                       @RequestBody @Valid SignalDtoRequest signalDtoRequest) throws SignalAppUnauthorizedException {
        return service.add(sessionId, signalDtoRequest);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void put(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
             @RequestBody @Valid SignalDtoRequest signalDtoRequest, @PathVariable int id)
            throws SignalAppUnauthorizedException, SignalAppDataException {
        service.update(sessionId, signalDtoRequest, id);
    }

    @DeleteMapping("/{id}")
    void delete(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                @PathVariable int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        service.delete(sessionId, id);
    }

    @GetMapping(path = "/{id}/data", produces = MediaType.APPLICATION_JSON_VALUE)
    List<SignalDataDto> getData(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                @PathVariable int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        return service.getData(sessionId, id);
    }

    @PostMapping(path = "/wav/{fileName}", consumes = "audio/wave")
    void postWav(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                 @PathVariable String fileName, @RequestBody byte[] data)
            throws UnsupportedAudioFileException, SignalAppUnauthorizedException, IOException {
        service.importWav(sessionId, fileName, data);
    }

}
