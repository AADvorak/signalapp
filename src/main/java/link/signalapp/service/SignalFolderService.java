package link.signalapp.service;

import link.signalapp.error.SignalAppNotFoundException;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.model.Folder;
import link.signalapp.model.Signal;
import link.signalapp.repository.FolderRepository;
import link.signalapp.repository.SignalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignalFolderService extends ServiceBase {

    private final SignalRepository signalRepository;
    private final FolderRepository folderRepository;

    public List<Integer> getFolderIds(int id)
            throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        return getSignalByIdAndUserId(id, getUserFromContext().getId()).getFolders()
                .stream().map(Folder::getId).toList();
    }

    public void addFolder(int id, int folderId)
            throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        int userId = getUserFromContext().getId();
        Signal signal = getSignalByIdAndUserId(id, userId);
        if (signal.getFolders().stream().anyMatch(folder -> folder.getId() == folderId)) {
            return;
        }
        Folder folder = folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(SignalAppNotFoundException::new);
        signal.getFolders().add(folder);
        try {
            signalRepository.save(signal);
        } catch (DataIntegrityViolationException ignore) {}
    }

    public void deleteFolder(int id, int folderId)
            throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        int userId = getUserFromContext().getId();
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
