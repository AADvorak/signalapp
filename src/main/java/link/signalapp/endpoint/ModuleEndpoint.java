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

import jakarta.validation.Valid;

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
    public List<ModuleDtoResponse> getAll() {
        return moduleService.getAll();
    }
    
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ModuleDtoResponse post(@RequestBody @Valid ModuleDtoRequest module)
            throws SignalAppDataException, SignalAppUnauthorizedException {
        return moduleService.add(module);
    }
    
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ModuleDtoResponse put(@RequestBody @Valid EditModuleDtoRequest module, @PathVariable Integer id)
            throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        return moduleService.update(module, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) throws SignalAppNotFoundException, SignalAppUnauthorizedException {
        moduleService.delete(id);
    }

    @GetMapping(path = "/{id}/html", produces = MediaType.TEXT_HTML_VALUE)
    public String getHtml(@PathVariable Integer id) throws SignalAppNotFoundException {
        return moduleService.getFile(id, "html");
    }

    @GetMapping(path = "/{id}/js", produces = "application/javascript")
    public String getJs(@PathVariable Integer id) throws SignalAppNotFoundException {
        return moduleService.getFile(id, "js");
    }

    @PutMapping(path = "/{id}/html", consumes = MediaType.TEXT_HTML_VALUE)
    public void putHtml(@PathVariable Integer id, @RequestBody String data)
            throws SignalAppNotFoundException, IOException, SignalAppUnauthorizedException {
        moduleService.writeFile(id, "html", data);
    }

    @PutMapping(path = "/{id}/js", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void putJs(@PathVariable Integer id, @RequestBody String data)
            throws SignalAppNotFoundException, IOException, SignalAppUnauthorizedException {
        moduleService.writeFile(id, "js", data);
    }

}
