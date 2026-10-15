package com.RR.DisSpeak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    /** Mostra la pagina principale dell'applicazione. */
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    /** Mostra la pagina dedicata ai server. */
    @GetMapping("/servers")
    public String servers() {
        return "servers";
    }

    /** Mostra la pagina dedicata ai canali. */
    @GetMapping("/channels")
    public String channels() {
        return "channels";
    }

    /** Mostra la pagina dedicata ai messaggi. */
    @GetMapping("/messages")
    public String messages() {
        return "messages";
    }

    /** Mostra la pagina di accesso. */
    @GetMapping("/login")
    public String login() {return "login";}
}
