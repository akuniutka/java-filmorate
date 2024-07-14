package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.getRandomFilm;

class FilmServiceImplTest {

    private final FilmService filmService;

    FilmServiceImplTest() {
        this.filmService = new FilmServiceImpl(new InMemoryFilmStorage());
    }

    @Test
    void shouldReturnFilmWithIdAssignedWhenCreate() {
        final Film film = getRandomFilm();
        final String name = film.getName();
        final String description = film.getDescription();
        final LocalDate releaseDate = film.getReleaseDate();
        final int duration = film.getDuration();

        final Film savedFilm = filmService.create(film);

        assertAll("Film created with errors",
                () -> assertNotNull(savedFilm.getId(), "Film ID should be not null"),
                () -> assertEquals(name, savedFilm.getName(), "Wrong name"),
                () -> assertEquals(description, savedFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, savedFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, savedFilm.getDuration(), "Wrong duration")
        );
    }

    @Test
    void shouldReturnFilmsWhenFindAll() {
        final Film film = getRandomFilm();
        final Collection<Film> expectedFilms = List.of(film);

        final Long filmId = filmService.create(film).getId();
        final Collection<Film> films = filmService.findAll();

        film.setId(filmId);
        assertEquals(expectedFilms.size(), films.size(), "Wrong films list");
        assertTrue(films.containsAll(expectedFilms), "Wrong films list");
    }

    @Test
    void shouldReturnUpdatedFilmWhenUpdate() {
        final Film oldFilm = getRandomFilm();
        final Long filmId = filmService.create(oldFilm).getId();
        final Film newFilm = getRandomFilm();
        newFilm.setId(filmId);

        final Film savedFilm = filmService.update(newFilm);

        assertEquals(newFilm, savedFilm, "Film updated with errors");
    }

    @Test
    void shouldThrowWhenUpdateAndFilmNotExist() {
        final Film oldFilm = getRandomFilm();
        final Long filmId = filmService.create(oldFilm).getId();
        final Film newFilm = getRandomFilm();
        newFilm.setId(filmId + 1);

        Exception exception = assertThrows(NotFoundException.class, () -> filmService.update(newFilm));
        assertEquals("Cannot find model 'film' with id = " + (filmId + 1), exception.getMessage(),
                "Wrong exception message");
    }
}