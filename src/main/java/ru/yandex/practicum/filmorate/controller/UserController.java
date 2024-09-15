package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
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
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.RecommendationService;
import ru.yandex.practicum.filmorate.service.api.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController extends BaseController {

    private final UserService userService;
    private final RecommendationService recommendationService;
    private final UserMapper mapper;
    private final FilmMapper filmMapper;
    private final EventMapper eventMapper;

    @PostMapping
    public UserDto createUser(
            @Valid @RequestBody final NewUserDto newUserDto,
            final HttpServletRequest request
    ) {
        logRequest(request, newUserDto);
        final User user = mapper.mapToUser(newUserDto);
        final UserDto userDto = mapper.mapToDto(userService.createUser(user));
        logResponse(request, userDto);
        return userDto;
    }

    @GetMapping("/{id}")
    public UserDto getUser(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final UserDto dto = mapper.mapToDto(userService.getUser(id));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    public Collection<UserDto> getUsers(
            final HttpServletRequest request
    ) {
        logRequest(request);
        Collection<UserDto> dtos = mapper.mapToDto(userService.getUsers());
        logResponse(request, dtos);
        return dtos;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable final long id,
            @PathVariable final long friendId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        userService.addFriend(id, friendId);
        logResponse(request);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable final long id,
            @PathVariable final long friendId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        userService.deleteFriend(id, friendId);
        logResponse(request);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Collection<UserDto> dtos = mapper.mapToDto(userService.getFriends(id));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(
            @PathVariable final long id,
            @PathVariable final long otherId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Collection<UserDto> dtos = mapper.mapToDto(userService.getCommonFriends(id, otherId));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/{id}/recommendations")
    public Collection<FilmDto> getRecommendedFilms(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        Collection<FilmDto> dtos = filmMapper.mapToDto(recommendationService.getRecommended(id));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/{id}/feed")
    public Collection<EventDto> getFeed(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Collection<EventDto> dtos = eventMapper.mapToDto(userService.getEvents(id));
        logRequest(request, dtos);
        return dtos;
    }

    @PutMapping
    public UserDto updateUser(
            @Valid @RequestBody final UpdateUserDto updateUserDto,
            final HttpServletRequest request
    ) {
        logRequest(request, updateUserDto);
        final User user = mapper.mapToUser(updateUserDto);
        final UserDto userDto = mapper.mapToDto(userService.updateUser(user));
        logResponse(request, userDto);
        return userDto;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        userService.deleteUserById(id);
        logResponse(request);
    }
}
