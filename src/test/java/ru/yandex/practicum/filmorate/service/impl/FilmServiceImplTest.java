package ru.yandex.practicum.filmorate.service.impl;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mem.FilmInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.mem.GenreInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.mem.UserInMemoryStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        this.filmStorage = new FilmInMemoryStorage();
        this.filmService = new FilmServiceImpl(
                filmStorage,
                new GenreServiceImpl(new GenreInMemoryStorage()),
                new UserServiceImpl(new UserInMemoryStorage())
        );
    }

    @Test
    void shouldThroeWhenCreateFilmAndFilmNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> filmService.createFilm(null));
        assertEquals("Cannot create film: is null", exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldReturnFilmWithIdAssignedWhenCreateFilm() {
        final Film film = getRandomFilm();
        final String name = film.getName();
        final String description = film.getDescription();
        final LocalDate releaseDate = film.getReleaseDate();
        final Integer duration = film.getDuration();

        final Film createdFilm = filmService.createFilm(film);

        final Film savedFilm = filmStorage.findById(createdFilm.getId()).orElseThrow();
        assertAll("Film created with errors",
                () -> assertNotNull(createdFilm.getId(), "Film ID should be not null"),
                () -> assertEquals(name, createdFilm.getName(), "Wrong name"),
                () -> assertEquals(description, createdFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, createdFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, createdFilm.getDuration(), "Wrong duration"),
                () -> assertEquals(createdFilm.getId(), savedFilm.getId(), "Wrong film ID"),
                () -> assertEquals(name, savedFilm.getName(), "Wrong name"),
                () -> assertEquals(description, savedFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, savedFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, savedFilm.getDuration(), "Wrong duration")
        );
    }

    @Test
    void shouldReturnFilmWithIdAssignedWhenCreateFilmAndIdNotNull() {
        final Film film = getRandomFilm();
        final long filmId = filmService.createFilm(film).getId();
        final Film otherFilm = getRandomFilm();
        otherFilm.setId(filmId);
        final String name = otherFilm.getName();
        final String description = otherFilm.getDescription();
        final LocalDate releaseDate = otherFilm.getReleaseDate();
        final Integer duration = otherFilm.getDuration();

        final Film createdFilm = filmService.createFilm(otherFilm);

        final Film savedFilm = filmStorage.findById(createdFilm.getId()).orElseThrow();
        assertAll("Film created with errors",
                () -> assertNotEquals(filmId, createdFilm.getId(), "Wrong film ID"),
                () -> assertEquals(name, createdFilm.getName(), "Wrong name"),
                () -> assertEquals(description, createdFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, createdFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, createdFilm.getDuration(), "Wrong duration"),
                () -> assertEquals(createdFilm.getId(), savedFilm.getId(), "Wrong film ID"),
                () -> assertEquals(name, savedFilm.getName(), "Wrong name"),
                () -> assertEquals(description, savedFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, savedFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, savedFilm.getDuration(), "Wrong duration")
        );
    }

    @Test
    void shouldReturnFilmsWhenGetFilms() {
        final Film film = getRandomFilm();
        film.setId(faker.number().randomNumber());
        filmStorage.save(film);
        final Collection<Film> expectedFilms = List.of(film);

        final Collection<Film> films = filmService.getFilms();

        assertEquals(expectedFilms.size(), films.size(), "Wrong films list");
        assertTrue(films.containsAll(expectedFilms), "Wrong films list");
    }

    @Test
    void shouldThrowWhenUpdateFilmAndFilmNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> filmService.updateFilm(null));
        assertEquals("Cannot update film: is null", exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldReturnEmptyOptionalWhenUpdateFilmAndFilmNotExist() {
        final Film newFilm = getRandomFilm();
        final long filmId = faker.number().randomNumber();
        newFilm.setId(filmId);

        final Optional<Film> actual = filmService.updateFilm(newFilm);

        assertTrue(actual.isEmpty(), "should find no film to update with id = " + filmId);
    }

    @Test
    void shouldReturnUpdatedFilmWhenUpdateFilm() {
        final Film oldFilm = getRandomFilm();
        final long filmId = filmStorage.save(oldFilm).getId();
        final Film newFilm = getRandomFilm();
        newFilm.setId(filmId);
        final String name = newFilm.getName();
        final String description = newFilm.getDescription();
        final LocalDate releaseDate = newFilm.getReleaseDate();
        final Integer duration = newFilm.getDuration();

        final Film updatedFilm = filmService.updateFilm(newFilm).orElseThrow();

        final Film savedFilm = filmStorage.findById(filmId).orElseThrow();
        assertAll("Film updated with errors",
                () -> assertEquals(filmId, updatedFilm.getId(), "Wrong film ID"),
                () -> assertEquals(name, updatedFilm.getName(), "Wrong name"),
                () -> assertEquals(description, updatedFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, updatedFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, updatedFilm.getDuration(), "Wrong duration"),
                () -> assertEquals(filmId, savedFilm.getId(), "Wrong film ID"),
                () -> assertEquals(name, savedFilm.getName(), "Wrong name"),
                () -> assertEquals(description, savedFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, savedFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, savedFilm.getDuration(), "Wrong duration")
        );
    }

    @Test
    void shouldReturnEmptyOptionalWhenGetFilmAndFilmNotExist() {
        final long wrongId = faker.number().randomNumber();

        final Optional<Film> actual = filmService.getFilm(wrongId);

        assertTrue(actual.isEmpty(), "should find no film to update with id = " + wrongId);
    }

    @Test
    void shouldGetFilm() {
        final Film film = getRandomFilm();
        final String name = film.getName();
        final String description = film.getDescription();
        final LocalDate releaseDate = film.getReleaseDate();
        final Integer duration = film.getDuration();
        final long filmId = filmService.createFilm(film).getId();

        final Film foundFilm = filmService.getFilm(filmId).orElseThrow();

        assertAll("Film created with errors",
                () -> assertEquals(filmId, foundFilm.getId(), "Wrong film ID should be not null"),
                () -> assertEquals(name, foundFilm.getName(), "Wrong name"),
                () -> assertEquals(description, foundFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, foundFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, foundFilm.getDuration(), "Wrong duration")
        );
    }
}