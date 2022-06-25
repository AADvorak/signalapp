package com.example.signalapp.endpoint;

import java.util.List;

import com.example.signalapp.dto.response.IdDtoResponse;
import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.dto.request.SignalDtoRequest;
import com.example.signalapp.dto.response.SignalDtoResponse;
import com.example.signalapp.error.SignalAppDataException;
import com.example.signalapp.service.SignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *
 * @author anton
 */
@RestController
@RequestMapping("/signals")
@RequiredArgsConstructor
public class SignalEndpoint {

    private final SignalService service;
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<SignalDtoResponse> getAll() {
        return service.getAll();
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    IdDtoResponse post(@RequestBody @Valid SignalDtoRequest signalDtoRequest) {
        return service.add(signalDtoRequest);
    }
    
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void put(@RequestBody @Valid SignalDtoRequest signalDtoRequest, @PathVariable int id) {
        service.update(signalDtoRequest, id);
    }

    @DeleteMapping("/{id}")
    void deleteSignal(@PathVariable int id) {
        service.delete(id);
    }
    
    @GetMapping(path = "/{id}/data", produces = MediaType.APPLICATION_JSON_VALUE)
    List<SignalDataDto> getData(@PathVariable int id) throws SignalAppDataException {
        return service.getData(id);
    }
    
}
