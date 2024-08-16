package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.service.api.LikeService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.LikeStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {

    private final LikeStorage likeStorage;
    private final UserService userService;
    private final FilmService filmService;

    @Override
    public void createLike(final Long filmId, final Long userId) {
        assertExist(filmId, userId);
        likeStorage.save(filmId, userId);
    }

    @Override
    public void deleteLike(final Long filmId, final Long userId) {
        assertExist(filmId, userId);
        likeStorage.delete(filmId, userId);
    }

    private void assertExist(final Long filmId, final Long userId) {
        filmService.getFilm(filmId).orElseThrow(
                () -> new NotFoundException(Film.class, filmId)
        );
        userService.getUser(userId).orElseThrow(
                () -> new NotFoundException(User.class, userId)
        );
    }

    @Override
    public Collection<Film> getTopFilmsByLikes(final Long limit) {
        Objects.requireNonNull(limit, "Cannot get top films: quantity set to null");
        return likeStorage.findAllFilmIdOrderByLikesDesc(limit).stream()
                .map(filmService::getFilm)
                .map(Optional::orElseThrow)
                .toList();
    }
}
