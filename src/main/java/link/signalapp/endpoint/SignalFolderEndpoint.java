package link.signalapp.endpoint;

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
    public List<Integer> get(@PathVariable int id) {
        return signalFolderService.getFolderIds(id);
    }

    @PostMapping("/{folderId}")
    public void post(@PathVariable int id, @PathVariable int folderId) {
        signalFolderService.addFolder(id, folderId);
    }

    @DeleteMapping("/{folderId}")
    public void delete(@PathVariable int id, @PathVariable int folderId) {
        signalFolderService.deleteFolder(id, folderId);
    }
}
