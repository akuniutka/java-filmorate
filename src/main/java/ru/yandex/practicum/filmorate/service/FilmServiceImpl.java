package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private long lastUsedId;

    public FilmServiceImpl(final FilmStorage filmStorage, final UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.lastUsedId = 0L;
    }

    @Override
    public Film create(final Film film) {
        Objects.requireNonNull(film, "Cannot create film: is null");
        film.setId(++lastUsedId);
        film.setLikes(new HashSet<>());
        filmStorage.save(film);
        log.info("Created new film: {}", film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film update(final Film newFilm) {
        Objects.requireNonNull(newFilm, "Cannot update film: is null");
        final Long filmId = newFilm.getId();
        final Film oldFilm = filmStorage.findById(filmId).orElseThrow(() -> new NotFoundException("film", filmId));
        newFilm.setLikes(oldFilm.getLikes());
        filmStorage.save(newFilm);
        log.info("Updated film with id={}: {}", newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film findFilmById(final Long id) {
        return filmStorage.findById(id).orElseThrow(() -> new NotFoundException("film", id));
    }

    @Override
    public void addLike(final Long id, final Long userId) {
        final Film film = filmStorage.findById(id).orElseThrow(() -> new NotFoundException("film", id));
        final User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
        if (film.getLikes().contains(userId) && user.getLikedFilms().contains(id)) {
            log.info("User with id = {} has already liked film with id = {}", userId, id);
            return;
        }
        film.getLikes().add(userId);
        user.getLikedFilms().add(id);
        filmStorage.save(film);
        userStorage.save(user);
        log.info("User with id = {} liked film with id = {}", userId, id);
    }

    @Override
    public void deleteLike(final Long id, final Long userId) {
        final Film film = filmStorage.findById(id).orElseThrow(() -> new NotFoundException("film", id));
        final User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
        if (!film.getLikes().contains(userId) && !user.getLikedFilms().contains(id)) {
            log.info("Film with id = {} has no like from user with id = {} already", id, userId);
            return;
        }
        film.getLikes().remove(userId);
        user.getLikedFilms().remove(id);
        filmStorage.save(film);
        userStorage.save(user);
        log.info("Film with id = {} has no like from user with id = {} anymore", id, userId);
    }

    @Override
    public Collection<Film> getTopLiked(final Long count) {
        if (count == null || count < 1) {
            throw new IllegalArgumentException("Count should be a positive number");
        }
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing(film -> -film.getLikes().size()))
                .limit(count)
                .toList();
    }
}
