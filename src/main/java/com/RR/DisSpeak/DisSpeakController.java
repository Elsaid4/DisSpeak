package com.RR.DisSpeak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DisSpeakController {

    /** Restituisce un messaggio di benvenuto per verificare la disponibilità dell'API. */
    @GetMapping("/hello")
    public String sayHello() {
        return "{\"message\": \"Ciao dal Backend!\"}";
    }
}