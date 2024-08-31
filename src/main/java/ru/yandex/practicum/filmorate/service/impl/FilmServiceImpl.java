package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.DirectorService;
import ru.yandex.practicum.filmorate.service.api.EventService;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.service.api.GenreService;
import ru.yandex.practicum.filmorate.service.api.MpaService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

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
    public Collection<Film> getFilms() {
        return filmStorage.findAll();
    }

    @Override
    public Collection<Film> getTopFilmsByLikes(final long limit, final Long genreId, final Integer year) {
        return filmStorage.findAllOrderByLikesDesc(limit, genreId, year);
    }

    @Override
    public Collection<Film> getFilmsByDirectorId(final long directorId) {
        directorService.getDirector(directorId);
        return filmStorage.findAllByDirectorId(directorId);
    }

    @Override
    public Collection<Film> getFilmsByDirectorIdOrderByYear(final long directorId) {
        directorService.getDirector(directorId);
        return filmStorage.findAllByDirectorIdOrderByYear(directorId);
    }

    @Override
    public Collection<Film> getFilmsByDirectorIdOrderByLikes(final long directorId) {
        directorService.getDirector(directorId);
        return filmStorage.findAllByDirectorIdOrderByLikes(directorId);
    }

    @Override
    public Film getFilm(final long id) {
        return filmStorage.findById(id).orElseThrow(
                () -> new NotFoundException(Film.class, id)
        );
    }

    @Override
    public Film createFilm(final Film film) {
        Objects.requireNonNull(film, "Cannot create film: is null");
        validateCollections(film);
        final Film filmStored = filmStorage.save(film);
        log.info("Created new film: {}", filmStored);
        return filmStored;
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
    public void addLike(final long id, final long userId) {
        getFilm(id);
        userService.getUser(userId);
        // добавление события добавления лайка в таблицу events
        eventService.createEvent(EventType.LIKE, userId, Operation.ADD, id);
        filmStorage.addLike(id, userId);
    }

    @Override
    public Collection<Film> getRecommendations(long userId) {
        Set<Long> currentUserLikes = filmStorage.getLikesByUserId(userId);

        Collection<User> allUsers = userService.getUsers();

        User bestMatchUser = null;
        int maxIntersection = 0;

        for (User otherUser : allUsers) {
            if (otherUser.getId() == userId) {
                continue;
            }

            Set<Long> otherUserLikes = filmStorage.getLikesByUserId(otherUser.getId());
            int intersectionSize = getIntersectionSize(currentUserLikes, otherUserLikes);

            if (intersectionSize > maxIntersection) {
                maxIntersection = intersectionSize;
                bestMatchUser = otherUser;
            }
        }

        if (bestMatchUser != null) {
            Set<Long> bestMatchLikes = filmStorage.getLikesByUserId(bestMatchUser.getId());
            bestMatchLikes.removeAll(currentUserLikes);

            return bestMatchLikes.stream()
                    .map(filmStorage::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    private int getIntersectionSize(Set<Long> set1, Set<Long> set2) {
        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection.size();
    }

    @Override
    public void deleteLike(final long id, final long userId) {
        getFilm(id);
        userService.getUser(userId);
        // добавление события удаления лайка в таблицу events
        if (filmStorage.deleteLike(id, userId)) {
            eventService.createEvent(EventType.LIKE, userId, Operation.REMOVE, id);
        }
    }

    @Override
    public void deleteFilm(long id) {
        filmStorage.delete(id);
    }

    @Override
    public Collection<Film> searchFilmsByTitle(String query) {
        return filmStorage.searchFilmsByTitle(query);
    }

    @Override
    public Collection<Film> searchFilmsByDirectorName(String query) {
        return filmStorage.searchFilmsByDirectorName(query);
    }

    @Override
    public Collection<Film> searchFilmsByTitleAndDirectorName(String query) {
        return filmStorage.searchFilmsByTitleAndDirectorName(query);
    }

    @Override
    public Collection<Film> getCommonFilms(long id, long friendId) {
        userService.getUser(id);
        userService.getUser(friendId);
        return filmStorage.getCommonFilms(id, friendId);
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
