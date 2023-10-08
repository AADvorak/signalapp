package link.signalapp.service;

import link.signalapp.dto.request.EditModuleDtoRequest;
import link.signalapp.dto.request.ModuleDtoRequest;
import link.signalapp.dto.response.ModuleDtoResponse;
import link.signalapp.error.SignalAppDataErrorCode;
import link.signalapp.error.SignalAppDataException;
import link.signalapp.error.SignalAppNotFoundException;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.file.FileManager;
import link.signalapp.mapper.ModuleMapper;
import link.signalapp.model.Module;
import link.signalapp.model.User;
import link.signalapp.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService extends ServiceBase {

    private final ModuleRepository moduleRepository;
    private final FileManager fileManager;

    public List<ModuleDtoResponse> getAll() {
        User user = null;
        try {
            user = getUserFromContext();
        } catch (SignalAppUnauthorizedException ignored) {}
        List<Module> modules = user != null
                ? moduleRepository.findByUserIdIsNullOrUserIdEquals(user.getId())
                : moduleRepository.findByUserIdIsNull();
        return modules.stream().map(ModuleMapper.INSTANCE::moduleToDto).collect(Collectors.toList());
    }

    public ModuleDtoResponse add(ModuleDtoRequest moduleDtoRequest) throws SignalAppDataException, SignalAppUnauthorizedException {
        User user = getUserFromContext();
        Module module = ModuleMapper.INSTANCE.dtoToModule(moduleDtoRequest);
        module.setUser(user);
        try {
            return ModuleMapper.INSTANCE.moduleToDto(moduleRepository.save(module));
        } catch (DataIntegrityViolationException ex) {
            throw new SignalAppDataException(SignalAppDataErrorCode.MODULE_ALREADY_EXISTS);
        }
    }

    public ModuleDtoResponse update(EditModuleDtoRequest moduleDtoRequest, int id)
            throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        Module module = moduleRepository.findByIdAndUserId(id, getUserFromContext().getId())
                .orElseThrow(SignalAppNotFoundException::new)
                .setName(moduleDtoRequest.getName())
                .setContainer(moduleDtoRequest.getContainer())
                .setForMenu(moduleDtoRequest.isForMenu())
                .setTransformer(moduleDtoRequest.isTransformer());
        return ModuleMapper.INSTANCE.moduleToDto(moduleRepository.save(module));
    }

    @Transactional
    public void delete(int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        if (moduleRepository.deleteByIdAndUserId(id, getUserFromContext().getId()) == 0) {
            throw new SignalAppNotFoundException();
        }
        // todo delete files
    }

    public String getFile(int id, String extension) throws SignalAppNotFoundException {
        User user = null;
        try {
            user = getUserFromContext();
        } catch (SignalAppUnauthorizedException ignored) {}
        Module module = moduleRepository.findById(id).orElseThrow(SignalAppNotFoundException::new);
        if (user == null && module.getUser() != null || user != null && module.getUser() != null
                && user.getId() != module.getUser().getId()) {
            throw new SignalAppNotFoundException();
        }
        try {
            return fileManager.readModuleFromFile(module.getModule().toLowerCase(), extension);
        } catch (IOException e) {
            throw new SignalAppNotFoundException();
        }
    }

    public void writeFile(int id, String extension, String data) throws SignalAppNotFoundException,
            IOException, SignalAppUnauthorizedException {
        Module module = moduleRepository.findByIdAndUserId(id, getUserFromContext().getId())
                .orElseThrow(SignalAppNotFoundException::new);
        fileManager.writeModuleToFile(module.getModule(), extension, data);
    }
}
