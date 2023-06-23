package link.signalapp.service;

import link.signalapp.ApplicationProperties;
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
import link.signalapp.repository.UserTokenRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(SignalAppNotFoundException::new)
                .setName(moduleDtoRequest.getName())
                .setContainer(moduleDtoRequest.getContainer())
                .setForMenu(moduleDtoRequest.isForMenu())
                .setTransformer(moduleDtoRequest.isTransformer());
        return ModuleMapper.INSTANCE.moduleToDto(moduleRepository.save(module));
    }

    @Transactional
    public void delete(String token, int id) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        if (moduleRepository.deleteByIdAndUserId(id, getUserByToken(token).getId()) == 0) {
            throw new SignalAppNotFoundException();
        }
        // todo delete files
    }

    public String getFile(String token, int id, String extension) throws SignalAppNotFoundException {
        User user = null;
        try {
            user = getUserByToken(token);
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

    public void writeFile(String token, int id, String extension, String data) throws SignalAppNotFoundException,
            IOException, SignalAppUnauthorizedException {
        Module module = moduleRepository.findByIdAndUserId(id, getUserByToken(token).getId())
                .orElseThrow(SignalAppNotFoundException::new);
        fileManager.writeModuleToFile(module.getModule(), extension, data);
    }
}
