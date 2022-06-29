package com.example.signalapp.endpoint;

import java.util.List;

import com.example.signalapp.dto.request.ModuleDtoRequest;
import com.example.signalapp.dto.response.ModuleDtoResponse;
import com.example.signalapp.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *
 * @author anton
 */
@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleEndpoint {
    
    private final ModuleService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<ModuleDtoResponse> getAll() {
        return service.getAll();
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ModuleDtoResponse post(@RequestBody @Valid ModuleDtoRequest module) {
        return service.add(module);
    }
    
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ModuleDtoResponse put(@RequestBody @Valid ModuleDtoRequest module, @PathVariable Integer id) {
        return service.update(module, id);
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id) {
        service.delete(id);
    }

}
