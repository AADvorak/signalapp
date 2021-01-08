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
    
    private final SignalTitleRepository titleRepository;
    
    private final SignalDataRepository dataRepository;
    
    private final SignalDataService signalDataService;

    SignalController(SignalTitleRepository titleRepository, SignalDataRepository dataRepository, SignalDataService signalDataService) {
        this.titleRepository = titleRepository;
        this.dataRepository = dataRepository;
        this.signalDataService = signalDataService;
    }

    @GetMapping("/signals")
    List<SignalTitle> signals() {
        return titleRepository.findAllByOrderByCreateTimeDesc();
    }
    
    @PostMapping("/signals")
    SignalTitle newSignal(@RequestBody Signal newSignal) {
        SignalTitle newSignalTitle = titleRepository.save(newSignal.getTitle());
        signalDataService.insert(newSignal.getData(), newSignalTitle.getId());
        return newSignalTitle;
    }
    
    @PutMapping("/signals/{id}")
    SignalTitle replaceSignal(@RequestBody Signal newSignal, @PathVariable Integer id) {
        SignalTitle newSignalTitle = titleRepository.findById(id).map(title -> {
            title.setName(newSignal.getTitle().getName());
            title.setDescription(newSignal.getTitle().getDescription());
            return titleRepository.save(title);
        }).orElseGet(() -> {
            newSignal.getTitle().setId(id);
            return titleRepository.save(newSignal.getTitle());
        });
        signalDataService.replace(newSignal.getData(), id);
        return newSignalTitle;
    }

    @DeleteMapping("/signals/{id}")
    void deleteSignal(@PathVariable Integer id) {
        titleRepository.deleteById(id);
        signalDataService.deleteByTitleId(id);
    }
    
    @GetMapping("/signaldata/{id}")
    List<SignalDataXandY> signalData(@PathVariable Integer id) {
        return dataRepository.findByTitleId(id);
    }
    
}
