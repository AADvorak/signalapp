package link.signalapp.service;

import link.signalapp.ApplicationProperties;
import link.signalapp.error.SignalAppNotFoundException;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.model.Folder;
import link.signalapp.model.Signal;
import link.signalapp.repository.FolderRepository;
import link.signalapp.repository.SignalRepository;
import link.signalapp.repository.UserTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignalFolderService extends ServiceBase {

    private final SignalRepository signalRepository;
    private final FolderRepository folderRepository;

    public SignalFolderService(
            UserTokenRepository userTokenRepository,
            ApplicationProperties applicationProperties,
            SignalRepository signalRepository,
            FolderRepository folderRepository) {
        super(userTokenRepository, applicationProperties);
        this.signalRepository = signalRepository;
        this.folderRepository = folderRepository;
    }

    public List<Integer> getFolderIds(String token, int id)
            throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        return getSignalByIdAndUserId(id, getUserByToken(token).getId()).getFolders()
                .stream().map(Folder::getId).toList();
    }

    public void addFolder(String token, int id, int folderId)
            throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        int userId = getUserByToken(token).getId();
        Signal signal = getSignalByIdAndUserId(id, userId);
        if (signal.getFolders().stream().anyMatch(folder -> folder.getId() == folderId)) {
            return;
        }
        Folder folder = folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(SignalAppNotFoundException::new);
        signal.getFolders().add(folder);
        signalRepository.save(signal);
    }

    public void deleteFolder(String token, int id, int folderId)
            throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        int userId = getUserByToken(token).getId();
        Signal signal = getSignalByIdAndUserId(id, userId);
        Folder folder = signal.getFolders()
                .stream().filter(item -> item.getId() == folderId).findAny().orElse(null);
        if (folder == null) {
            return;
        }
        signal.getFolders().remove(folder);
        signalRepository.save(signal);
    }

    private Signal getSignalByIdAndUserId(int id, int userId) throws SignalAppNotFoundException {
        Signal signal = signalRepository.findByIdAndUserId(id, userId);
        if (signal == null) {
            throw new SignalAppNotFoundException();
        }
        return signal;
    }
}
