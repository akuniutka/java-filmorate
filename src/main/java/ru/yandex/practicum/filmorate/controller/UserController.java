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
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.FriendService;
import ru.yandex.practicum.filmorate.service.api.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final FriendService friendService;
    private final UserMapper mapper;

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable final Long id, @PathVariable final Long otherId) {
        log.info("Received GET at /users/{}/friends/common/{}", id, otherId);
        final Collection<UserDto> dtos = mapper.mapToDto(friendService.getCommonFriends(id, otherId));
        log.info("Responded to GET /users/{}/friends/common/{}: {}", id, otherId, dtos);
        return dtos;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable final Long id, @PathVariable final Long friendId) {
        log.info("Received PUT at /users/{}/friends/{}", id, friendId);
        friendService.createFriend(id, friendId);
        log.info("Responded to PUT /users/{}/friends/{} with no body", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable final Long id, @PathVariable final Long friendId) {
        log.info("Received DELETE at /users/{}/friends/{}", id, friendId);
        friendService.deleteFriend(id, friendId);
        log.info("Responded to DELETE /users/{}/friends/{} with no body", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable final Long id) {
        log.info("Received GET at /users/{}/friends", id);
        final Collection<UserDto> dtos = mapper.mapToDto(friendService.getFriendsByUserId(id));
        log.info("Responded to GET /users/{}/friends: {}", id, dtos);
        return dtos;
    }

    @GetMapping
    public Collection<UserDto> getUsers() {
        log.info("Received GET at /users");
        Collection<UserDto> dtos = mapper.mapToDto(userService.getUsers());
        log.info("Responded to GET /users: {}", dtos);
        return dtos;
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable final Long id) {
        log.info("Received GET at /users/{}", id);
        final UserDto dto = userService.getUser(id).map(mapper::mapToDto).orElseThrow(
                () -> new NotFoundException(User.class, id)
        );
        log.info("Responded to GET /users/{}: {}", id, dto);
        return dto;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody final NewUserDto newUserDto) {
        log.info("Received POST at /users");
        final User user = mapper.mapToUser(newUserDto);
        final UserDto userDto = mapper.mapToDto(userService.createUser(user));
        log.info("Responded to POST /users: {}", userDto);
        return userDto;
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody final UpdateUserDto updateUserDto) {
        log.info("Received PUT at /users");
        final User user = mapper.mapToUser(updateUserDto);
        final UserDto userDto = userService.updateUser(user).map(mapper::mapToDto).orElseThrow(
                () -> new NotFoundException(User.class, updateUserDto.getId())
        );
        log.info("Responded to PUT at /users: {}", userDto);
        return userDto;
    }
}
