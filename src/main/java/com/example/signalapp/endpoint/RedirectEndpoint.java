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

    @GetMapping("/signalmanager")
    public RedirectView redirectToSignalManager(RedirectAttributes attributes,
                                                @RequestParam Map<String, String> requestParams) {
        return redirectTo(attributes, "signalmanager", requestParams);
    }

    @GetMapping("/signalgenerator")
    public RedirectView redirectToSignalGenerator(RedirectAttributes attributes) {
        return redirectTo(attributes, "signalgenerator");
    }

    @GetMapping("/signalrecorder")
    public RedirectView redirectToSignalRecorder(RedirectAttributes attributes) {
        return redirectTo(attributes, "signalrecorder");
    }

    @GetMapping("/signin")
    public RedirectView redirectToSignIn(RedirectAttributes attributes) {
        return redirectTo(attributes, "signin");
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
