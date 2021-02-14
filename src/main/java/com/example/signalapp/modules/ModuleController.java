/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp.modules;

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
public class ModuleController {
    
    private final ModuleRepository repository;

    ModuleController(ModuleRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/modules")
    List<Module> all() {
        return repository.findAll();
    }
    
    @PostMapping("/modules")
    Module newModule(@RequestBody Module newModule) {
        return repository.save(newModule);
    }
    
    @PutMapping("/modules/{id}")
    Module replaceModule(@RequestBody Module newModule, @PathVariable Integer id) {
        return repository.findById(id).map(module -> {
            module.setName(newModule.getName());
            module.setModule(newModule.getModule());
            module.setContainer(newModule.getContainer());
            module.setForMenu(newModule.isForMenu());
            module.setTransformer(newModule.isTransformer());
            return repository.save(module);
        }).orElseGet(() -> {
            newModule.setId(id);
            return repository.save(newModule);
        });
    }

    @DeleteMapping("/modules/{id}")
    void deleteModule(@PathVariable Integer id) {
        repository.deleteById(id);
    }

}
