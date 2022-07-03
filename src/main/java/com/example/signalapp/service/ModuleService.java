package com.example.signalapp.service;

import com.example.signalapp.ApplicationProperties;
import com.example.signalapp.dto.request.EditModuleDtoRequest;
import com.example.signalapp.dto.request.ModuleDtoRequest;
import com.example.signalapp.dto.response.ModuleDtoResponse;
import com.example.signalapp.error.SignalAppDataErrorCode;
import com.example.signalapp.error.SignalAppDataException;
import com.example.signalapp.error.SignalAppNotFoundException;
import com.example.signalapp.error.SignalAppUnauthorizedException;
import com.example.signalapp.file.FileManager;
import com.example.signalapp.mapper.ModuleMapper;
import com.example.signalapp.model.Module;
import com.example.signalapp.model.User;
import com.example.signalapp.repository.ModuleRepository;
import com.example.signalapp.repository.UserTokenRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleService extends ServiceBase {

    private final ModuleRepository moduleRepository;

    private final FileManager fileManager;

    public ModuleService(UserTokenRepository userTokenRepository, ApplicationProperties applicationProperties,
                         ModuleRepository moduleRepository, FileManager fileManager) {
        super(userTokenRepository, applicationProperties);
        this.moduleRepository = moduleRepository;
        this.fileManager = fileManager;
    }

    public List<ModuleDtoResponse> getAll(String token) {
        User user = null;
        try {
            user = getUserByToken(token);
        } catch (SignalAppUnauthorizedException ignored) {}
        List<Module> modules = user != null
                ? moduleRepository.findByUserIdIsNullOrUserIdEquals(user.getId())
                : moduleRepository.findByUserIdIsNull();
        return modules.stream().map(ModuleMapper.INSTANCE::moduleToDto).collect(Collectors.toList());
    }

    public ModuleDtoResponse add(String token, ModuleDtoRequest moduleDtoRequest) throws SignalAppDataException, SignalAppUnauthorizedException {
        User user = getUserByToken(token);
        Module module = ModuleMapper.INSTANCE.dtoToModule(moduleDtoRequest);
        module.setUser(user);
        try {
            return ModuleMapper.INSTANCE.moduleToDto(moduleRepository.save(module));
        } catch (DataIntegrityViolationException ex) {
            throw new SignalAppDataException(SignalAppDataErrorCode.MODULE_ALREADY_EXISTS);
        }
    }

    public ModuleDtoResponse update(String token, EditModuleDtoRequest moduleDtoRequest, int id)
            throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        Module module = moduleRepository.findByIdAndUserId(id, getUserByToken(token).getId())
                .orElseThrow(SignalAppNotFoundException::new);
        module.setName(moduleDtoRequest.getName());
        module.setContainer(moduleDtoRequest.getContainer());
        module.setForMenu(moduleDtoRequest.isForMenu());
        module.setTransformer(moduleDtoRequest.isTransformer());
        return ModuleMapper.INSTANCE.moduleToDto(moduleRepository.save(module));
    }

    public void delete(String token, int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        if (moduleRepository.deleteByIdAndUserId(id, getUserByToken(token).getId()) == 0) {
            throw new SignalAppNotFoundException();
        }
        // todo delete files
    }

    public String getFile(int id, String extension) throws SignalAppNotFoundException {
        Module module = moduleRepository.findById(id).orElseThrow(SignalAppNotFoundException::new);
        try {
            return fileManager.readModuleFromFile(module.getModule().toLowerCase(), extension);
        } catch (IOException e) {
            throw new SignalAppNotFoundException();
        }
    }

    public void writeFile(String token, int id, String extension, String data) throws SignalAppNotFoundException,
            IOException, SignalAppUnauthorizedException {
        Module module = moduleRepository.findByIdAndUserId(id, getUserByToken(token).getId())
                .orElseThrow(SignalAppNotFoundException::new);
        fileManager.writeModuleToFile(module.getModule(), extension, data);
    }
}
