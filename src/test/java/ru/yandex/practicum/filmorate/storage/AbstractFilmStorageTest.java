package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.assertFilmEquals;
import static ru.yandex.practicum.filmorate.TestModels.assertFilmListEquals;
import static ru.yandex.practicum.filmorate.TestModels.cloneFilm;
import static ru.yandex.practicum.filmorate.TestModels.getRandomFilm;

public abstract class AbstractFilmStorageTest {

    protected FilmStorage filmStorage;

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
    void shouldDeleteFilm() {
        final List<Film> expectedFilms = preloadData();
        final long id = expectedFilms.get(1).getId();
        expectedFilms.remove(1);

        filmStorage.delete(id);
        final List<Film> actualFilms = new ArrayList<>(filmStorage.findAll());

        assertFilmListEquals(expectedFilms, actualFilms);
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
            expectedFilms.add(film);
            savedFilms.add(savedFilm);
        }
        assertFilmListEquals(expectedFilms, savedFilms);
        return expectedFilms;
    }
}
