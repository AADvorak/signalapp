/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.endpoint;

import java.util.List;

import com.example.signalapp.dto.IdDtoResponse;
import com.example.signalapp.dto.SignalDataDto;
import com.example.signalapp.dto.SignalDtoRequest;
import com.example.signalapp.dto.SignalDtoResponse;
import com.example.signalapp.service.SignalService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author anton
 */
@RestController
@RequestMapping("/signals")
public class SignalEndpoint {

    private final SignalService service;
    
    SignalEndpoint(SignalService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<SignalDtoResponse> getAll() {
        return service.getAll();
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    IdDtoResponse post(@RequestBody SignalDtoRequest signalDtoRequest) {
        return service.add(signalDtoRequest);
    }
    
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    void put(@RequestBody SignalDtoRequest signalDtoRequest, @PathVariable int id) {
        service.update(signalDtoRequest, id);
    }

    @DeleteMapping("/{id}")
    void deleteSignal(@PathVariable int id) {
        service.delete(id);
    }
    
    @GetMapping(path = "/{id}/data", produces = MediaType.APPLICATION_JSON_VALUE)
    List<SignalDataDto> getData(@PathVariable int id) {
        return service.getData(id);
    }
    
}
