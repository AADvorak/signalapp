package com.example.signalapp.dao;

import com.example.signalapp.model.Module;

import java.util.List;

public interface ModuleDao {

    List<Module> getAll();

    Module insert(Module module);

    Module update(Module module, int id);

    void delete(int id);

}
