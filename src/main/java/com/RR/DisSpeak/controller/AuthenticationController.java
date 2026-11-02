package com.RR.DisSpeak.controller;

import com.RR.DisSpeak.dto.SignInDto;
import com.RR.DisSpeak.model.entity.User;
import com.RR.DisSpeak.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    /** Verifica le credenziali, marca l'utente online e restituisce il suo profilo. */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignInDto Sigin) {
        User user = userService.loadUser(Sigin);
        if (user != null){
            userService.connect(user);
            return ResponseEntity.ok(user);
        }else return ResponseEntity.badRequest().build();
    }

    /** Registra l'utente, lo marca online e restituisce il profilo creato. */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignInDto register) {
        User user = userService.registerUser(register);
        userService.connect(user);
        return ResponseEntity.ok(user);
    }

}
