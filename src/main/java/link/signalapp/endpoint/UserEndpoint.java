package link.signalapp.endpoint;

import link.signalapp.dto.request.*;
import link.signalapp.dto.response.ResponseWithToken;
import link.signalapp.dto.response.UserDtoResponse;
import link.signalapp.error.SignalAppDataException;
import link.signalapp.error.SignalAppUnauthorizedException;
import link.signalapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserEndpoint extends EndpointBase {

    private final UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDtoResponse post(@Valid @RequestBody UserDtoRequest user, HttpServletResponse response) throws Exception {
        ResponseWithToken<UserDtoResponse> responseWithToken = userService.register(user);
        setCookieWithTokenToResponse(responseWithToken.getToken(), response);
        return responseWithToken.getResponse();
    }

    @DeleteMapping("/me")
    public void deleteCurrentUser() throws SignalAppUnauthorizedException {
        userService.deleteCurrentUser();
    }

    @GetMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDtoResponse getCurrentUserInfo() throws SignalAppUnauthorizedException {
        return userService.getCurrentUserInfo();
    }

    @PutMapping(path = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDtoResponse editCurrentUser(
            @Valid @RequestBody EditUserDtoRequest request
    ) throws SignalAppUnauthorizedException, SignalAppDataException {
        return userService.editCurrentUser(request);
    }

    @PutMapping(path = "/me/password", produces = MediaType.APPLICATION_JSON_VALUE)
    public void editCurrentUserPassword(
            @Valid @RequestBody ChangePasswordDtoRequest request
    ) throws SignalAppUnauthorizedException, SignalAppDataException {
        userService.changePasswordCurrentUser(request);
    }

    @PostMapping("/confirm")
    public void postMailConfirm(
            @Valid @RequestBody EmailConfirmDtoRequest request
    ) throws SignalAppUnauthorizedException, MessagingException, SignalAppDataException {
        userService.makeUserEmailConfirmation(request);
    }

    @PostMapping(path = "/restore")
    public void restorePassword(
            @Valid @RequestBody RestorePasswordDtoRequest request
    ) throws MessagingException, SignalAppDataException {
        userService.restorePassword(request);
    }

}
