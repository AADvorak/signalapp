package link.signalapp.endpoint;

import java.io.IOException;
import java.util.List;

import link.signalapp.dto.request.EditModuleDtoRequest;
import link.signalapp.dto.request.ModuleDtoRequest;
import link.signalapp.dto.response.ModuleDtoResponse;
import link.signalapp.error.SignalAppDataException;
import link.signalapp.error.SignalAppNotFoundException;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 *
 * @author anton
 */
@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleEndpoint extends EndpointBase {
    
    private final ModuleService moduleService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModuleDtoResponse> getAll(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId) {
        return moduleService.getAll(sessionId);
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ModuleDtoResponse post(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                  @RequestBody @Valid ModuleDtoRequest module)
            throws SignalAppDataException, SignalAppUnauthorizedException {
        return moduleService.add(sessionId, module);
    }
    
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ModuleDtoResponse put(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                                 @RequestBody @Valid EditModuleDtoRequest module, @PathVariable Integer id)
            throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        return moduleService.update(sessionId, module, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                       @PathVariable Integer id) throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        moduleService.delete(sessionId, id);
    }

    @GetMapping(path = "/{id}/html", produces = MediaType.TEXT_HTML_VALUE)
    public String getHtml(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                          @PathVariable Integer id) throws SignalAppNotFoundException {
        return moduleService.getFile(sessionId, id, "html");
    }

    @GetMapping(path = "/{id}/js", produces = "application/javascript")
    public String getJs(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                        @PathVariable Integer id) throws SignalAppNotFoundException {
        return moduleService.getFile(sessionId, id, "js");
    }

    @PutMapping(path = "/{id}/html", consumes = MediaType.TEXT_HTML_VALUE)
    public void putHtml(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                        @PathVariable Integer id, @RequestBody String data)
            throws SignalAppNotFoundException, IOException, SignalAppUnauthorizedException {
        moduleService.writeFile(sessionId, id, "html", data);
    }

    @PutMapping(path = "/{id}/js", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void putJs(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId,
                      @PathVariable Integer id, @RequestBody String data)
            throws SignalAppNotFoundException, IOException, SignalAppUnauthorizedException {
        moduleService.writeFile(sessionId, id, "js", data);
    }

}
