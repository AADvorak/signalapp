package link.signalapp.service;

import link.signalapp.ApplicationProperties;
import link.signalapp.dto.request.FolderDtoRequest;
import link.signalapp.dto.response.FolderDtoResponse;
import link.signalapp.error.*;
import link.signalapp.file.FileManager;
import link.signalapp.mapper.FolderMapper;
import link.signalapp.model.Folder;
import link.signalapp.model.Signal;
import link.signalapp.repository.FolderRepository;
import link.signalapp.repository.SignalRepository;
import link.signalapp.repository.UserTokenRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FolderService extends ServiceBase {

    public static final int MAX_USER_FOLDERS_NUMBER = 10;

    private final FolderRepository folderRepository;
    private final SignalRepository signalRepository;
    private final FileManager fileManager;

    public FolderService(UserTokenRepository userTokenRepository, ApplicationProperties applicationProperties,
                         FolderRepository folderRepository, SignalRepository signalRepository,
                         FileManager fileManager) {
        super(userTokenRepository, applicationProperties);
        this.folderRepository = folderRepository;
        this.signalRepository = signalRepository;
        this.fileManager = fileManager;
    }

    public List<FolderDtoResponse> get(String token) throws SignalAppUnauthorizedException {
        return folderRepository.findByUserId(getUserByToken(token).getId())
                .stream().map(FolderMapper.INSTANCE::folderToDto).toList();
    }

    public FolderDtoResponse add(String token, FolderDtoRequest request)
            throws SignalAppUnauthorizedException, SignalAppConflictException, SignalAppDataException {
        int userId = getUserByToken(token).getId();
        if (folderRepository.countByUserId(userId) >= MAX_USER_FOLDERS_NUMBER) {
            throw new SignalAppConflictException(SignalAppErrorCode.TOO_MANY_FOLDERS_CREATED,
                    new MaxNumberExceptionParams(MAX_USER_FOLDERS_NUMBER));
        }
        Folder folder = FolderMapper.INSTANCE.dtoToFolder(request)
                .setUserId(userId);
        try {
            return FolderMapper.INSTANCE.folderToDto(folderRepository.save(folder));
        } catch (DataIntegrityViolationException exc) {
            throw new SignalAppDataException(SignalAppDataErrorCode.FOLDER_NAME_ALREADY_EXISTS);
        }
    }

    public FolderDtoResponse update(String token, FolderDtoRequest request, int id)
            throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        Folder folder = folderRepository.findByIdAndUserId(id, getUserByToken(token).getId())
                .orElseThrow(SignalAppNotFoundException::new)
                .setName(request.getName())
                .setDescription(request.getDescription());
        return FolderMapper.INSTANCE.folderToDto(folderRepository.save(folder));
    }

    @Transactional
    public void delete(String token, int id, boolean deleteSignals)
            throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        int userId = getUserByToken(token).getId();
        Folder folder = folderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(SignalAppNotFoundException::new);
        folderRepository.delete(folder);
        if (deleteSignals) {
            folder.getSignals().forEach(signal -> deleteSignal(userId, signal.getId()));
        }
    }

    private void deleteSignal(int userId, int signalId) {
        try {
            signalRepository.deleteByIdAndUserId(signalId, userId);
            fileManager.deleteSignalData(userId, signalId);
        } catch (Exception ignore) {}
    }
}
