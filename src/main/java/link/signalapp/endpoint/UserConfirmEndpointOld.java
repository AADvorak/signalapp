package link.signalapp.endpoint;

import link.signalapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserConfirmEndpointOld {

    private final UserService userService;

    @GetMapping(path = "/confirm-old/{code}")
    public String getMailConfirm(@RequestHeader(name = "Host", defaultValue = "localhost:8080") String host,
                                 @PathVariable String code, Model model) {
        model.addAttribute("host", host);
        return userService.confirmEmail(code);
    }

}
