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

import java.util.*;
import java.util.stream.Collectors;

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
    public Collection<Film> getTopFilmsByLikes(final long limit, final Long genreId, final Integer year) {
        return filmStorage.findAllOrderByLikesDesc(limit, genreId, year);
    }

    @Override
    public Collection<Film> getFilmsByDirectorId(final long directorId) {
        return filmStorage.findAllByDirectorId(directorId);
    }

    @Override
    public Collection<Film> getFilmsByDirectorIdOrderByYear(final long directorId) {
        return filmStorage.findAllByDirectorIdOrderByYear(directorId);
    }

    @Override
    public Collection<Film> getFilmsByDirectorIdOrderByLikes(final long directorId) {
        return filmStorage.findAllByDirectorIdOrderByLikes(directorId);
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
        assertFilmExist(id);
        assertUserExist(userId);
        filmStorage.deleteLike(id, userId);
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
