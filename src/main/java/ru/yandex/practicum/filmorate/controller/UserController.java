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
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.EventService;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.service.api.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final EventService eventService;
    private final FilmService filmService;
    private final UserMapper mapper;
    private final EventMapper eventMapper;

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable final long id, @PathVariable final long otherId) {
        log.info("Received GET at /users/{}/friends/common/{}", id, otherId);
        final Collection<UserDto> dtos = mapper.mapToDto(userService.getCommonFriends(id, otherId));
        log.info("Responded to GET /users/{}/friends/common/{}: {}", id, otherId, dtos);
        return dtos;
    }

    @GetMapping("/{id}/feed")
    public Collection<EventDto> getFeed(@PathVariable final long id) {
        log.info("Received GET at /users/{}/feed", id);
        final Collection<EventDto> dtos = eventMapper.mapToDto(eventService.getEvents(id));
        log.info("Responded to GET /users/{}/feed: {}", id, dtos);
        return dtos;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable final long id, @PathVariable final long friendId) {
        log.info("Received PUT at /users/{}/friends/{}", id, friendId);
        userService.addFriend(id, friendId);
        log.info("Responded to PUT /users/{}/friends/{} with no body", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable final long id, @PathVariable final long friendId) {
        log.info("Received DELETE at /users/{}/friends/{}", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Responded to DELETE /users/{}/friends/{} with no body", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable final long id) {
        log.info("Received GET at /users/{}/friends", id);
        final Collection<UserDto> dtos = mapper.mapToDto(userService.getFriends(id));
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
    public UserDto getUser(@PathVariable final long id) {
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

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommendations(@PathVariable long id) {
        log.info("Received GET request at /film/{}\", id");
        Collection<Film> dtos = filmService.getRecommendations(id);
        log.info("Responded to GET /film: {}", id);
        return dtos;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Received DELETE request at /Users/{}", userId);
        userService.deleteUserById(userId);
        log.info("User with id {} deleted successfully", userId);
    }

}
