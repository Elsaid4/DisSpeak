package com.RR.DisSpeak.service;

import com.RR.DisSpeak.config.Status;
import com.RR.DisSpeak.dto.SignInDto;
import com.RR.DisSpeak.model.entity.User;
import com.RR.DisSpeak.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    /** Inizializza il servizio con il repository degli utenti. */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Imposta online l'utente esistente e restituisce la versione salvata. */
    public User connect(User user) {
        if (user == null || user.getId() == null) {
            return null;
        }
        Optional<User> checkUser = userRepository.findById(user.getId());
        if (checkUser.isPresent()) {
            User storedUser = checkUser.get();
            storedUser.setStatus(Status.ONLINE);
            return userRepository.save(storedUser);
        }
        return null;
    }

    /** Imposta offline l'utente esistente e restituisce la versione salvata. */
    public User disconnect(User user) {
        if (user == null || user.getId() == null) {
            return null;
        }
        Optional<User> checkUser = userRepository.findById(user.getId());
        if (checkUser.isPresent()) {
            User storedUser = checkUser.get();
            storedUser.setStatus(Status.OFFLINE);
            return userRepository.save(storedUser);
        }
        return null;
    }

    /** Restituisce gli utenti il cui stato risulta online. */
    public List<User> getConnectedUsers() {
        return userRepository.findByStatus(Status.ONLINE);
    }

    /** Cerca un utente usando le credenziali ricevute, oppure restituisce null se non valido. */
    public User loadUser(SignInDto signInDto) {
        if (signInDto == null) {
            return null;
        }
        Optional<User> checkUser = userRepository.findByUsernameAndPassword(signInDto.getUsername(), signInDto.getPassword());
        if (checkUser.isPresent()) {
            return checkUser.get();
        }
        return null;
    }

    /** Crea e salva un utente con i dati di registrazione e stato iniziale offline. */
    public User registerUser(SignInDto signInDto) {
        return userRepository.save(new User(
                signInDto.getUsername(),
                signInDto.getPassword(),
                signInDto.getFullName(),
                signInDto.getEmail(),
                Status.OFFLINE
        ));
    }

    /** Cerca un utente tramite ID e restituisce null se l'ID è nullo o non trovato. */
    public User find(Long id) {
        if (id == null) {
            return null;
        }
        Optional<User> checkUser = userRepository.findById(id);
        if (checkUser.isPresent()) {
            return checkUser.get();
        }
        return null;
    }
}
