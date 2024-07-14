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
        return userService.findCommonFriendsByUserIds(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable final Long id, @PathVariable final Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable final Long id, @PathVariable final Long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable final Long id) {
        return userService.findFriendsByUserId(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody final User user) {
        return userService.create(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PutMapping
    public User update(@Valid @RequestBody final User user) {
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable final Long id) {
        return userService.findUserById(id);
    }
}
