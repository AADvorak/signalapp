package com.example.signalapp.service;

import com.example.signalapp.dto.ModuleDtoRequest;
import com.example.signalapp.dto.ModuleDtoResponse;
import com.example.signalapp.model.Module;
import com.example.signalapp.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository repository;

    public List<ModuleDtoResponse> getAll() {
        return repository.findAll().stream().map(module -> new ModuleDtoResponse(module.getId(), module.getModule(),
                module.getName(), module.getContainer(), module.isForMenu(), module.isTransformer()))
                .collect(Collectors.toList());
    }

    public ModuleDtoResponse add(ModuleDtoRequest moduleDtoRequest) {
        Module module = repository.save(new Module(null, moduleDtoRequest.getModule(), moduleDtoRequest.getName(),
                moduleDtoRequest.getContainer(), moduleDtoRequest.isForMenu(), moduleDtoRequest.isTransformer()));
        return new ModuleDtoResponse(module.getId(), module.getModule(),
                module.getName(), module.getContainer(), module.isForMenu(), module.isTransformer());
    }

    public ModuleDtoResponse update(ModuleDtoRequest moduleDtoRequest, int id) {
        Module module = new Module(id, moduleDtoRequest.getModule(), moduleDtoRequest.getName(),
                moduleDtoRequest.getContainer(), moduleDtoRequest.isForMenu(), moduleDtoRequest.isTransformer());
        Module updatedModule = repository.findById(id).map(existingModule -> {
            existingModule.setName(module.getName());
            existingModule.setModule(module.getModule());
            existingModule.setContainer(module.getContainer());
            existingModule.setForMenu(module.isForMenu());
            existingModule.setTransformer(module.isTransformer());
            return repository.save(existingModule);
        }).orElseGet(() -> {
            module.setId(id);
            return repository.save(module);
        });
        return new ModuleDtoResponse(updatedModule.getId(), updatedModule.getModule(), updatedModule.getName(),
                updatedModule.getContainer(), updatedModule.isForMenu(), updatedModule.isTransformer());
    }

    public void delete(int id) {
        repository.deleteById(id);
    }
}
