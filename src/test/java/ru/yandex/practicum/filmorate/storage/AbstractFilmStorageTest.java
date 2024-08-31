package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.assertFilmEquals;
import static ru.yandex.practicum.filmorate.TestModels.assertFilmListEquals;
import static ru.yandex.practicum.filmorate.TestModels.cloneFilm;
import static ru.yandex.practicum.filmorate.TestModels.getRandomFilm;
import static ru.yandex.practicum.filmorate.TestModels.getRandomUser;

public abstract class AbstractFilmStorageTest {

    protected FilmStorage filmStorage;
    protected UserStorage userStorage;

    @AfterEach
    void tearDown() {
        filmStorage.deleteAll();
    }

    @Test
    void shouldReturnCorrectFilmList() {
        final List<Film> expectedFilms = preloadData();

        final List<Film> actualFilms = new ArrayList<>(filmStorage.findAll());

        assertFilmListEquals(expectedFilms, actualFilms);
    }

    @Test
    void shouldReturnCorrectFilmById() {
        final List<Film> expectedFilms = preloadData();

        final Optional<Film> actual = filmStorage.findById(expectedFilms.get(1).getId());

        assertTrue(actual.isPresent(), "optional should not be empty");
        assertFilmEquals(expectedFilms.get(1), actual.get());
    }

    @Test
    void shouldReturnEmptyOptionalForUnknownId() {
        final List<Film> expectedFilms = preloadData();
        final long id = expectedFilms.getFirst().getId() - 1;

        final Optional<Film> actual = filmStorage.findById(id);

        assertTrue(actual.isEmpty(), "should find no film for id = " + id);
    }

    @Test
    void shouldUpdateFilm() {
        final List<Film> expectedFilms = preloadData();
        final long id = expectedFilms.get(1).getId();
        final Film expectedFilm = getRandomFilm();
        expectedFilm.setId(id);

        final Film savedFilm = filmStorage.update(cloneFilm(expectedFilm)).orElseThrow();
        final Film actualFilm = filmStorage.findById(id).orElseThrow();

        expectedFilm.setGenres(Collections.emptySet());
        expectedFilm.setDirectors(Collections.emptySet());
        assertFilmEquals(expectedFilm, savedFilm);
        assertFilmEquals(expectedFilm, actualFilm);
    }

    @Test
    void shouldReturnEmptyOptionalWhenUpdateUnknownFilm() {
        final List<Film> expectedFilms = preloadData();
        final Film expectedFilm = getRandomFilm();
        expectedFilm.setId(expectedFilms.getFirst().getId() - 1);

        final Optional<Film> savedFilm = filmStorage.update(cloneFilm(expectedFilm));

        assertTrue(savedFilm.isEmpty(), "should find no film to update with id = " + expectedFilm.getId());
    }

    @Test
    void shouldReturnTrueWhenDeleteFilm() {
        final List<Film> expectedFilms = preloadData();
        final long id = expectedFilms.get(1).getId();
        expectedFilms.remove(1);

        assertTrue(filmStorage.delete(id));
        final List<Film> actualUsers = new ArrayList<>(filmStorage.findAll());

        assertFilmListEquals(expectedFilms, actualUsers);
    }

    @Test
    void shouldReturnFalseWhenDeleteUnknownFilm() {
        final List<Film> expectedFilms = preloadData();
        final long id = expectedFilms.getFirst().getId() - 1;

        assertFalse(filmStorage.delete(id));
        final List<Film> actualUsers = new ArrayList<>(filmStorage.findAll());

        assertFilmListEquals(expectedFilms, actualUsers);
    }

    @Test
    void shouldReturnZeroLikesForNewFilm() {
        final Film film = getRandomFilm();

        final Film savedFilm = filmStorage.save(film);
        final Film actualFilm = filmStorage.findById(savedFilm.getId()).orElseThrow();

        assertAll("wrong number of likes for film",
                () -> assertEquals(0L, savedFilm.getLikes()),
                () -> assertEquals(0L, actualFilm.getLikes())
        );
    }

    @Test
    void shouldIncreaseFilmLikesWhenAddNewLike() {
        final Film film = getRandomFilm();
        final User user = getRandomUser();
        final long filmId = filmStorage.save(film).getId();
        final long userId = userStorage.save(user).getId();

        filmStorage.addLike(filmId, userId);
        final Film actualFilm = filmStorage.findById(filmId).orElseThrow();

        assertEquals(1L, actualFilm.getLikes(), "wrong number of likes");
    }

    @Test
    void shouldNotIncreaseFilmLikesWhenAddDuplicateLike() {
        final Film film = getRandomFilm();
        final User user = getRandomUser();
        final long filmId = filmStorage.save(film).getId();
        final long userId = userStorage.save(user).getId();

        filmStorage.addLike(filmId, userId);
        filmStorage.addLike(filmId, userId);
        final Film actualFilm = filmStorage.findById(filmId).orElseThrow();

        assertEquals(1L, actualFilm.getLikes(), "wrong number of likes");
    }

    @Test
    void shouldDecreaseFilmLikesWhenDeleteExistingLike() {
        final Film film = getRandomFilm();
        final User user = getRandomUser();
        final long filmId = filmStorage.save(film).getId();
        final long userId = userStorage.save(user).getId();

        filmStorage.addLike(filmId, userId);
        filmStorage.deleteLike(filmId, userId);
        final Film actualFilm = filmStorage.findById(filmId).orElseThrow();

        assertEquals(0L, actualFilm.getLikes(), "wrong number of likes");
    }

    @Test
    void shouldNotDecreaseFilmLikesWhenDeleteNotExistingLike() {
        final Film film = getRandomFilm();
        final User user = getRandomUser();
        final long filmId = filmStorage.save(film).getId();
        final long userId = userStorage.save(user).getId();

        filmStorage.deleteLike(filmId, userId);
        final Film actualFilm = filmStorage.findById(filmId).orElseThrow();

        assertEquals(0L, actualFilm.getLikes(), "wrong number of likes");
    }

    @Disabled
    @Test
    void shouldDecreaseFilmLikesWhenDeleteUser() {
        // TODO: implement test
    }

    @Test
    void shouldDeleteAllFilms() {
        preloadData();

        filmStorage.deleteAll();
        final Collection<Film> actual = filmStorage.findAll();

        assertTrue(actual.isEmpty(), "should no film remain");
    }

    protected List<Film> preloadData() {
        final List<Film> expectedFilms = new ArrayList<>();
        final List<Film> savedFilms = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Film film = getRandomFilm();
            Film savedFilm = filmStorage.save(cloneFilm(film));
            film.setId(savedFilm.getId());
            film.setGenres(Collections.emptySet());
            film.setDirectors(Collections.emptySet());
            expectedFilms.add(film);
            savedFilms.add(savedFilm);
        }
        assertFilmListEquals(expectedFilms, savedFilms);
        return expectedFilms;
    }
}
