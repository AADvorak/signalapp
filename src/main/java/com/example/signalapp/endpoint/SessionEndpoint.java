package com.example.signalapp.endpoint;

import com.example.signalapp.dto.request.LoginDtoRequest;
import com.example.signalapp.dto.response.ResponseWithToken;
import com.example.signalapp.dto.response.UserDtoResponse;
import com.example.signalapp.error.SignalAppUnauthorizedException;
import com.example.signalapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
    public void delete(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId) throws SignalAppUnauthorizedException {
        userService.logout(sessionId);
    }

}
