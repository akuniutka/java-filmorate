package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.api.DirectorService;
import ru.yandex.practicum.filmorate.service.api.EventService;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.service.api.GenreService;
import ru.yandex.practicum.filmorate.service.api.MpaService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final EventService eventService;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final DirectorService directorService;

    @Override
    public Film createFilm(final Film film) {
        Objects.requireNonNull(film, "Cannot create film: is null");
        validateCollections(film);
        final Film filmStored = filmStorage.save(film);
        log.info("Created new film: {}", filmStored);
        return filmStored;
    }

    @Override
    public Film getFilm(final long id) {
        return filmStorage.findById(id).orElseThrow(
                () -> new NotFoundException(Film.class, id)
        );
    }

    @Override
    public Collection<Film> getFilms() {
        return filmStorage.findAll();
    }

    @Override
    public Collection<Film> getTopFilms(final long count, final Long genreId, final Integer year) {
        if (genreId != null && year != null) {
            return filmStorage.findByGenreIdAndReleaseYearOrderByLikesDesc(genreId, year, count);
        } else if (genreId != null) {
            return filmStorage.findByGenreIdOrderByLikesDesc(genreId, count);
        } else if (year != null) {
            return filmStorage.findByReleaseYearOrderByLikesDesc(year, count);
        } else {
            return filmStorage.findAllOrderByLikesDesc(count);
        }
    }

    @Override
    public Collection<Film> getFilmsByDirectorId(final long directorId, final String sortBy) {
        directorService.getDirector(directorId);
        if (sortBy == null) {
            return filmStorage.findByDirectorId(directorId);
        } else if ("likes".equalsIgnoreCase(sortBy)) {
            return filmStorage.findByDirectorIdOrderByLikesDesc(directorId);
        } else if ("year".equalsIgnoreCase(sortBy)) {
            return filmStorage.findByDirectorIdOrderByYearAsc(directorId);
        } else {
            throw new ValidationException("Check parameter to sort films by (you send %s)".formatted(sortBy));
        }
    }

    @Override
    public Film updateFilm(final Film film) {
        Objects.requireNonNull(film, "Cannot update film: is null");
        validateCollections(film);
        final Optional<Film> filmStored = filmStorage.update(film);
        filmStored.ifPresent(f -> log.info("Updated film: {}", filmStored.get()));
        return filmStored.orElseThrow(
                () -> new NotFoundException(Film.class, film.getId())
        );
    }

    @Override
    public void addLike(final long id, final long userId, final int mark) {
        getFilm(id);
        userService.getUser(userId);
        filmStorage.addLike(id, userId, mark);
        eventService.createEvent(EventType.LIKE, userId, Operation.ADD, id);
    }

    @Override
    public void deleteLike(final long id, final long userId) {
        getFilm(id);
        userService.getUser(userId);
        if (filmStorage.deleteLike(id, userId)) {
            eventService.createEvent(EventType.LIKE, userId, Operation.REMOVE, id);
        }
    }

    @Override
    public void deleteFilm(long id) {
        filmStorage.delete(id);
    }

    @Override
    public Collection<Film> getFilmsByTitleAndDirectorName(final String query, final String by) {
        if ("director,title".equalsIgnoreCase(by) || "title,director".equalsIgnoreCase(by)) {
            return filmStorage.findByNameOrDirectorNameOrderByLikesDesc(query);
        } else if ("title".equalsIgnoreCase(by)) {
            return filmStorage.findByNameOrderByLikesDesc(query);
        } else if ("director".equalsIgnoreCase(by)) {
            return filmStorage.findByDirectorNameOrderByLikesDesc(query);
        } else {
            throw new ValidationException("Check parameter to filter films by (you send %s)".formatted(by));
        }
    }

    @Override
    public Collection<Film> getLikedFilms(long userId) {
        userService.getUser(userId);
        return filmStorage.findByUserId(userId);
    }

    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {
        userService.getUser(userId);
        userService.getUser(friendId);
        final Set<Film> commonFilms = new LinkedHashSet<>(filmStorage.findByUserId(userId));
        commonFilms.retainAll(filmStorage.findByUserId(friendId));
        return commonFilms;
    }

    private void validateCollections(final Film film) {
        if (film.getMpa() != null) {
            mpaService.validateId(film.getMpa().getId());
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreService.validateId(film.getGenres().stream()
                    .map(Genre::getId)
                    .toList()
            );
        }
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            directorService.validateId(film.getDirectors().stream()
                    .map(Director::getId)
                    .toList()
            );
        }
    }
}
