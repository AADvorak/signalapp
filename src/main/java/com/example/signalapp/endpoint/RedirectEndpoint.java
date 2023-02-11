package com.example.signalapp.endpoint;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
@RequestMapping("/")
public class RedirectEndpoint {

    @GetMapping("/signal-manager")
    public RedirectView redirectToSignalManager(RedirectAttributes attributes,
                                                @RequestParam Map<String, String> requestParams) {
        return redirectTo(attributes, "signal-manager", requestParams);
    }

    @GetMapping("/signal-generator")
    public RedirectView redirectToSignalGenerator(RedirectAttributes attributes) {
        return redirectTo(attributes, "signal-generator");
    }

    @GetMapping("/signal-recorder")
    public RedirectView redirectToSignalRecorder(RedirectAttributes attributes) {
        return redirectTo(attributes, "signal-recorder");
    }

    @GetMapping("/signin")
    public RedirectView redirectToSignIn(RedirectAttributes attributes) {
        return redirectTo(attributes, "signin");
    }

    @GetMapping("/signup")
    public RedirectView redirectToSignUp(RedirectAttributes attributes) {
        return redirectTo(attributes, "signup");
    }

    @GetMapping("/user-settings")
    public RedirectView redirectToUserSettings(RedirectAttributes attributes) {
        return redirectTo(attributes, "user-settings");
    }

    @GetMapping("/change-password")
    public RedirectView redirectToChangePassword(RedirectAttributes attributes) {
        return redirectTo(attributes, "change-password");
    }

    @GetMapping("/restore-password")
    public RedirectView redirectToRestorePassword(RedirectAttributes attributes) {
        return redirectTo(attributes, "restore-password");
    }

    @GetMapping("/signal/{id}")
    public RedirectView redirectToSignal(RedirectAttributes attributes, @PathVariable String id) {
        return redirectTo(attributes, "signal/" + id);
    }

    private RedirectView redirectTo(RedirectAttributes attributes, String path, Map<String, String> requestParams) {
        attributes.addAttribute("goto", path);
        requestParams.keySet().forEach((key) -> attributes.addAttribute(key, requestParams.get(key)));
        return new RedirectView("/");
    }

    private RedirectView redirectTo(RedirectAttributes attributes, String path) {
        attributes.addAttribute("goto", path);
        return new RedirectView("/");
    }
}
