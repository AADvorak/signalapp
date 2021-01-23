/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.signals;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author anton
 */
@RestController
public class SignalController {
    
    private final SignalRepository signalRepository;
    private final SignalDataRepository signalDataRepository;
    
    SignalController(SignalRepository signalRepository, SignalDataRepository signalDataRepository) {
        this.signalRepository = signalRepository;
        this.signalDataRepository = signalDataRepository;
    }

    @GetMapping("/signals")
    List<SignalIdNameDescription> signals() {
        return signalRepository.findAllIdNameDescription();
    }
    
    @PostMapping("/signals")
    Id newSignal(@RequestBody Signal newSignal) {
        newSignal.setSignalToData();
        return new Id(signalRepository.save(newSignal).getId());
    }
    
    @PutMapping("/signals/{id}")
    void replaceSignal(@RequestBody Signal newSignal, @PathVariable Integer id) {
        signalRepository.findById(id).map(signal -> {
            signal.setName(newSignal.getName());
            signal.setDescription(newSignal.getDescription());
            signal.setData(newSignal.getData());
            return signalRepository.save(signal);
        }).orElseGet(() -> {
            newSignal.setId(id);
            return signalRepository.save(newSignal);
        });
    }

    @DeleteMapping("/signals/{id}")
    void deleteSignal(@PathVariable Integer id) {
        signalRepository.deleteById(id);
    }
    
    @GetMapping("/signals/{id}/data")
    List<SignalDataXandY> signalData(@PathVariable Integer id) {
        return signalDataRepository.findBySignalId(id);
    }
    
}
