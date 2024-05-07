package link.signalapp.endpoint;

import link.signalapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserConfirmEndpoint {

    private final UserService userService;

    @GetMapping(path = "/confirm/{code}")
    public RedirectView getMailConfirm(@PathVariable String code) {
        return new RedirectView("/" + userService.confirmEmail(code));
    }
}
