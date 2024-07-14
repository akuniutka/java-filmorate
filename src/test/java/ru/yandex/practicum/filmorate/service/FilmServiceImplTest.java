package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.faker;
import static ru.yandex.practicum.filmorate.TestModels.getRandomFilm;

class FilmServiceImplTest {

    private static final String WRONG_MESSAGE = "Wrong exception message";
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    FilmServiceImplTest() {
        this.filmStorage = new InMemoryFilmStorage();
        this.filmService = new FilmServiceImpl(filmStorage);
    }

    @Test
    void shouldThroeWhenCreateAndFilmNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> filmService.create(null));
        assertEquals("Cannot create film: is null", exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldReturnFilmWithIdAssignedWhenCreate() {
        final Film film = getRandomFilm();
        final String name = film.getName();
        final String description = film.getDescription();
        final LocalDate releaseDate = film.getReleaseDate();
        final Integer duration = film.getDuration();

        final Film createdFilm = filmService.create(film);

        final Film savedFilm = filmStorage.findById(createdFilm.getId()).orElseThrow();
        assertAll("Film created with errors",
                () -> assertNotNull(createdFilm.getId(), "Film ID should be not null"),
                () -> assertEquals(name, createdFilm.getName(), "Wrong name"),
                () -> assertEquals(description, createdFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, createdFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, createdFilm.getDuration(), "Wrong duration"),
                () -> assertTrue(createdFilm.getLikes().isEmpty(), "Wrong list of likes"),
                () -> assertEquals(createdFilm.getId(), savedFilm.getId(), "Wrong film ID"),
                () -> assertEquals(name, savedFilm.getName(), "Wrong name"),
                () -> assertEquals(description, savedFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, savedFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, savedFilm.getDuration(), "Wrong duration"),
                () -> assertTrue(savedFilm.getLikes().isEmpty(), "Wrong list of likes")
        );
    }

    @Test
    void shouldReturnFilmsWhenFindAll() {
        final Film film = getRandomFilm();
        film.setId(faker.number().randomNumber());
        filmStorage.save(film);
        final Collection<Film> expectedFilms = List.of(film);

        final Collection<Film> films = filmService.findAll();

        assertEquals(expectedFilms.size(), films.size(), "Wrong films list");
        assertTrue(films.containsAll(expectedFilms), "Wrong films list");
    }

    @Test
    void shouldThrowWhenUpdateAndFilmNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> filmService.update(null));
        assertEquals("Cannot update film: is null", exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldThrowWhenUpdateAndFilmNotExist() {
        final Film newFilm = getRandomFilm();
        final Long filmId = faker.number().randomNumber();
        newFilm.setId(filmId);

        Exception exception = assertThrows(NotFoundException.class, () -> filmService.update(newFilm));
        assertEquals("Cannot find model 'film' with id = " + filmId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldReturnUpdatedFilmWhenUpdate() {
        final Film oldFilm = getRandomFilm();
        final Long filmId = faker.number().randomNumber();
        final Set<Long> likes = Set.of(faker.number().randomNumber());
        oldFilm.setId(filmId);
        oldFilm.setLikes(likes);
        filmStorage.save(oldFilm);
        final Film newFilm = getRandomFilm();
        newFilm.setId(filmId);
        final String name = newFilm.getName();
        final String description = newFilm.getDescription();
        final LocalDate releaseDate = newFilm.getReleaseDate();
        final Integer duration = newFilm.getDuration();

        final Film updatedFilm = filmService.update(newFilm);

        final Film savedFilm = filmStorage.findById(filmId).orElseThrow();
        assertAll("Film updated with errors",
                () -> assertEquals(filmId, updatedFilm.getId(), "Wrong film ID"),
                () -> assertEquals(name, updatedFilm.getName(), "Wrong name"),
                () -> assertEquals(description, updatedFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, updatedFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, updatedFilm.getDuration(), "Wrong duration"),
                () -> assertEquals(likes.size(), updatedFilm.getLikes().size(), "Wrong list of likes"),
                () -> assertTrue(updatedFilm.getLikes().containsAll(likes), "Wrong list of likes"),
                () -> assertEquals(filmId, savedFilm.getId(), "Wrong film ID"),
                () -> assertEquals(name, savedFilm.getName(), "Wrong name"),
                () -> assertEquals(description, savedFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, savedFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, savedFilm.getDuration(), "Wrong duration"),
                () -> assertEquals(likes.size(), savedFilm.getLikes().size(), "Wrong list of likes"),
                () -> assertTrue(savedFilm.getLikes().containsAll(likes), "Wrong list of likes")
        );
    }
}