package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Received POST at /users: {}", user);
        final User savedUser = userService.create(user);
        log.info("Responded to POST at /users: {}", savedUser);
        return savedUser;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Received GET at /users");
        final Collection<User> users = userService.findAll();
        log.info("Responded to GET at /users: {}", users);
        return users;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Received PUT at /users: {}", user);
        final User savedUser = userService.update(user);
        log.info("Responded to PUT at /users: {}", savedUser);
        return savedUser;
    }
}
