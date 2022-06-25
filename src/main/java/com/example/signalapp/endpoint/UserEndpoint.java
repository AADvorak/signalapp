package com.example.signalapp.endpoint;

import com.example.signalapp.dto.request.UserDtoRequest;
import com.example.signalapp.dto.response.ResponseWithToken;
import com.example.signalapp.dto.response.UserDtoResponse;
import com.example.signalapp.error.SignalAppDataException;
import com.example.signalapp.error.SignalAppUnauthorizedException;
import com.example.signalapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserEndpoint extends EndpointBase {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDtoResponse post(@Valid @RequestBody UserDtoRequest user, HttpServletResponse response) throws SignalAppDataException {
        ResponseWithToken<UserDtoResponse> responseWithToken = userService.register(user);
        response.addCookie(new Cookie(JAVASESSIONID, responseWithToken.getToken()));
        return responseWithToken.getResponse();
    }

    @DeleteMapping("/me")
    public void deleteCurrentUser(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId) throws SignalAppUnauthorizedException {
        userService.deleteCurrentUser(sessionId);
    }

    @GetMapping("/me")
    public UserDtoResponse getCurrentUserInfo(@CookieValue(name = JAVASESSIONID, defaultValue = "") String sessionId) throws SignalAppUnauthorizedException {
        return userService.getCurrentUserInfo(sessionId);
    }

}
