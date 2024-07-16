package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable final Long id, @PathVariable final Long otherId) {
        log.info("Received GET at /users/{}/friends/common/{}", id, otherId);
        final Collection<User> friends = userService.findCommonFriendsByUserIds(id, otherId);
        log.info("Responded to GET /users/{}/friends/common/{}: {}", id, otherId, friends);
        return friends;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable final Long id, @PathVariable final Long friendId) {
        log.info("Received PUT at /users/{}/friends/{}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable final Long id, @PathVariable final Long friendId) {
        log.info("Received DELETE at /users/{}/friends/{}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable final Long id) {
        log.info("Received GET at /users/{}/friends", id);
        final Collection<User> friends = userService.findFriendsByUserId(id);
        log.info("Responded to GET /users/{}/friends: {}", id, friends);
        return friends;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable final Long id) {
        log.info("Received GET at /users/{}", id);
        final User user = userService.findUserById(id);
        log.info("Responded to GET /users/{}: {}", id, user);
        return user;
    }

    @PostMapping
    public User create(@Valid @RequestBody final User user) {
        log.info("Received POST at /users");
        final User createdUser = userService.create(user);
        log.info("Responded to POST /users: {}", createdUser);
        return createdUser;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Received GET at /users");
        Collection<User> users = userService.findAll();
        log.info("Responded to GET /users: {}", users);
        return users;
    }

    @PutMapping
    public User update(@Valid @RequestBody final User user) {
        log.info("Received PUT at /users");
        User updatedUser =  userService.update(user);
        log.info("Responded to PUT at /users: {}", updatedUser);
        return updatedUser;
    }
}
