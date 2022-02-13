/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.endpoint;

import java.util.List;

import com.example.signalapp.dto.ModuleDtoRequest;
import com.example.signalapp.dto.ModuleDtoResponse;
import com.example.signalapp.service.ModuleService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author anton
 */
@RestController
@RequestMapping("/modules")
public class ModuleEndpoint {
    
    private final ModuleService service;

    ModuleEndpoint(ModuleService service) {
        this.service = service;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<ModuleDtoResponse> getAll() {
        return service.getAll();
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ModuleDtoResponse post(@RequestBody ModuleDtoRequest module) {
        return service.add(module);
    }
    
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ModuleDtoResponse put(@RequestBody ModuleDtoRequest module, @PathVariable Integer id) {
        return service.update(module, id);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id) {
        service.delete(id);
    }

}
