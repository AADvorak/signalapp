package com.example.signalapp.daoimpl;

import com.example.signalapp.dao.ModuleDao;
import com.example.signalapp.model.Module;
import com.example.signalapp.repository.ModuleRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModuleDaoImpl implements ModuleDao {

    private final ModuleRepository repository;

    public ModuleDaoImpl(ModuleRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Module> getAll() {
        return repository.findAll();
    }

    @Override
    public Module insert(Module module) {
        return repository.save(module);
    }

    @Override
    public Module update(Module module, int id) {
        return repository.findById(id).map(existingModule -> {
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
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }
}
