package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    @Override
    public Collection<Film> getFilms() {
        return filmStorage.findAll();
    }

    @Override
    public Collection<Film> getTopFilmsByLikes(final long limit) {
        return filmStorage.findAllOrderByLikesDesc(limit);
    }

    @Override
    public Optional<Film> getFilm(final long id) {
        return filmStorage.findById(id);
    }

    @Override
    public Film createFilm(final Film film) {
        Objects.requireNonNull(film, "Cannot create film: is null");
        final Film filmStored = filmStorage.save(film);
        log.info("Created new film: {}", filmStored);
        return filmStored;
    }

    @Override
    public Optional<Film> updateFilm(final Film film) {
        Objects.requireNonNull(film, "Cannot update film: is null");
        final Optional<Film> filmStored = filmStorage.update(film);
        filmStored.ifPresent(f -> log.info("Updated film: {}", filmStored.get()));
        return filmStored;
    }

    @Override
    public void addLike(final long id, final long userId) {
        assertFilmExist(id);
        assertUserExist(userId);
        filmStorage.addLike(id, userId);
    }

    @Override
    public void deleteLike(final long id, final long userId) {
        assertFilmExist(id);
        assertUserExist(userId);
        filmStorage.deleteLike(id, userId);
    }

    @Override
    public Collection<Film> getCommonFilms(long id, long friendId) {
        assertUserExist(id);
        assertUserExist(friendId);
        return filmStorage.getCommonFilms(id, friendId);
    }

    private void assertFilmExist(final long id) {
        filmStorage.findById(id).orElseThrow(() -> new NotFoundException(Film.class, id));
    }

    private void assertUserExist(final long userId) {
        userService.getUser(userId).orElseThrow(() -> new NotFoundException(User.class, userId));
    }
}
