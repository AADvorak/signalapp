package com.example.signalapp.endpoint;

import com.example.signalapp.service.UserService;
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
        String result = userService.confirmEmail(code);
        return new RedirectView("/" + (result.equals(UserService.EMAIL_CONFIRM_ERROR)
                ? result : "user-settings"));
    }
}
