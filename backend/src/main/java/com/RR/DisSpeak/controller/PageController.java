package com.RR.DisSpeak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/servers")
    public String servers() {
        return "servers";
    }

    @GetMapping("/channels")
    public String channels() {
        return "channels";
    }

    @GetMapping("/messages")
    public String messages() {
        return "messages";
    }
}
