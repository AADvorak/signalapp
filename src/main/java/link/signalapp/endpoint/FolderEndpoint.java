package link.signalapp.endpoint;

import link.signalapp.dto.request.FolderDtoRequest;
import link.signalapp.dto.response.FolderDtoResponse;
import link.signalapp.error.SignalAppConflictException;
import link.signalapp.error.SignalAppDataException;
import link.signalapp.error.SignalAppNotFoundException;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FolderEndpoint extends EndpointBase {

    private final FolderService folderService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FolderDtoResponse> get(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId
    ) throws SignalAppUnauthorizedException {
        return folderService.get(sessionId);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public FolderDtoResponse post(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @RequestBody @Valid FolderDtoRequest request
    ) throws SignalAppConflictException, SignalAppUnauthorizedException, SignalAppDataException {
        return folderService.add(sessionId, request);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void put(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @RequestBody @Valid FolderDtoRequest request,
            @PathVariable int id
    ) throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        folderService.update(sessionId, request, id);
    }

    @DeleteMapping("/{id}")
    void delete(
            @CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "false") boolean deleteSignals
    ) throws SignalAppUnauthorizedException, SignalAppNotFoundException {
        folderService.delete(sessionId, id, deleteSignals);
    }

}
