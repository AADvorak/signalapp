package link.signalapp.endpoint;

import link.signalapp.dto.request.LoginDtoRequest;
import link.signalapp.dto.response.ResponseWithToken;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionEndpoint extends EndpointBase {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDtoResponse post(@Valid @RequestBody LoginDtoRequest login, HttpServletResponse response) throws Exception {
        ResponseWithToken<UserDtoResponse> responseWithToken = userService.login(login);
        setCookieWithTokenToResponse(responseWithToken.getToken(), response);
        return responseWithToken.getResponse();
    }

    @DeleteMapping
    public void delete() throws SignalAppUnauthorizedException {
        userService.logout();
    }

}
