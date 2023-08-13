package link.signalapp.endpoint;

import link.signalapp.error.SignalAppNotFoundException;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.service.SignalFolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signals/{id}/folders")
@RequiredArgsConstructor
public class SignalFolderEndpoint extends EndpointBase {

    private final SignalFolderService signalFolderService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Integer> get(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @PathVariable int id
    ) throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        return signalFolderService.getFolderIds(sessionId, id);
    }

    @PostMapping("/{folderId}")
    public void post(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @PathVariable int id,
            @PathVariable int folderId
    ) throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        signalFolderService.addFolder(sessionId, id, folderId);
    }

    @DeleteMapping("/{folderId}")
    public void delete(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @PathVariable int id,
            @PathVariable int folderId
    ) throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        signalFolderService.deleteFolder(sessionId, id, folderId);
    }
}
