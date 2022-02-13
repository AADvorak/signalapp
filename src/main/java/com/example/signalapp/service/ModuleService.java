package com.example.signalapp.service;

import com.example.signalapp.dao.ModuleDao;
import com.example.signalapp.daoimpl.ModuleDaoImpl;
import com.example.signalapp.dto.ModuleDtoRequest;
import com.example.signalapp.dto.ModuleDtoResponse;
import com.example.signalapp.model.Module;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleService {

    private final ModuleDao moduleDao;

    public ModuleService(ModuleDaoImpl moduleDao) {
        this.moduleDao = moduleDao;
    }

    public List<ModuleDtoResponse> getAll() {
        return moduleDao.getAll().stream().map(module -> new ModuleDtoResponse(module.getId(), module.getModule(),
                module.getName(), module.getContainer(), module.isForMenu(), module.isTransformer()))
                .collect(Collectors.toList());
    }

    public ModuleDtoResponse add(ModuleDtoRequest moduleDtoRequest) {
        Module module = moduleDao.insert(new Module(null, moduleDtoRequest.getModule(), moduleDtoRequest.getName(),
                moduleDtoRequest.getContainer(), moduleDtoRequest.isForMenu(), moduleDtoRequest.isTransformer()));
        return new ModuleDtoResponse(module.getId(), module.getModule(),
                module.getName(), module.getContainer(), module.isForMenu(), module.isTransformer());
    }

    public ModuleDtoResponse update(ModuleDtoRequest moduleDtoRequest, int id) {
        Module module = moduleDao.update(new Module(id, moduleDtoRequest.getModule(), moduleDtoRequest.getName(),
                moduleDtoRequest.getContainer(), moduleDtoRequest.isForMenu(), moduleDtoRequest.isTransformer()), id);
        return new ModuleDtoResponse(module.getId(), module.getModule(),
                module.getName(), module.getContainer(), module.isForMenu(), module.isTransformer());
    }

    public void delete(int id) {
        moduleDao.delete(id);
    }
}
