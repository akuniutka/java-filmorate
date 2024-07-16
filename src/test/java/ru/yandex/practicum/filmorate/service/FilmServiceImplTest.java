package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.faker;
import static ru.yandex.practicum.filmorate.TestModels.getRandomFilm;
import static ru.yandex.practicum.filmorate.TestModels.getRandomUser;

class FilmServiceImplTest {

    private static final String WRONG_MESSAGE = "Wrong exception message";
    private final UserStorage userStorage;
    private final UserService userService;
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    FilmServiceImplTest() {
        this.userStorage = new InMemoryUserStorage();
        this.userService = new UserServiceImpl(userStorage);
        this.filmStorage = new InMemoryFilmStorage();
        this.filmService = new FilmServiceImpl(filmStorage, userStorage);
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
    void shouldReturnFilmWithIdAssignedWhenCreateAndIdNotNull() {
        final Film film = getRandomFilm();
        final Long filmId = filmService.create(film).getId();
        final Film otherFilm = getRandomFilm();
        otherFilm.setId(filmId);
        final String name = otherFilm.getName();
        final String description = otherFilm.getDescription();
        final LocalDate releaseDate = otherFilm.getReleaseDate();
        final Integer duration = otherFilm.getDuration();

        final Film createdFilm = filmService.create(otherFilm);

        final Film savedFilm = filmStorage.findById(createdFilm.getId()).orElseThrow();
        assertAll("Film created with errors",
                () -> assertNotEquals(filmId, createdFilm.getId(), "Wrong film ID"),
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

    @Test
    void shouldThrowWhenFindFilmByIdAndFilmNotExist() {
        final Long wrongId = faker.number().randomNumber();

        Exception exception = assertThrows(NotFoundException.class, () -> filmService.findFilmById(wrongId));
        assertEquals("Cannot find model 'film' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldFindFilmById() {
        final Film film = getRandomFilm();
        final String name = film.getName();
        final String description = film.getDescription();
        final LocalDate releaseDate = film.getReleaseDate();
        final Integer duration = film.getDuration();
        final Long filmId = filmService.create(film).getId();

        final Film foundFilm = filmService.findFilmById(filmId);

        assertAll("Film created with errors",
                () -> assertEquals(filmId, foundFilm.getId(), "Wrong film ID should be not null"),
                () -> assertEquals(name, foundFilm.getName(), "Wrong name"),
                () -> assertEquals(description, foundFilm.getDescription(), "Wrong description"),
                () -> assertEquals(releaseDate, foundFilm.getReleaseDate(), "Wrong release date"),
                () -> assertEquals(duration, foundFilm.getDuration(), "Wrong duration")
        );
    }

    @Test
    void shouldThrowWhenAddLikeAndFilmNotExist() {
        final Film film = getRandomFilm();
        final Long filmId = filmService.create(film).getId();
        final User user = getRandomUser();
        final Long userId = userService.create(user).getId();
        final Long wrongId = filmId + 1;

        Exception exception = assertThrows(NotFoundException.class, () -> filmService.addLike(wrongId, userId));
        assertEquals("Cannot find model 'film' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldThrowWhenAddLikeAndUserNotExist() {
        final Film film = getRandomFilm();
        final Long filmId = filmService.create(film).getId();
        final User user = getRandomUser();
        final Long userId = userService.create(user).getId();
        final Long wrongId = userId + 1;

        Exception exception = assertThrows(NotFoundException.class, () -> filmService.addLike(filmId, wrongId));
        assertEquals("Cannot find model 'user' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldAddLike() {
        final Film film = getRandomFilm();
        final Long filmId = filmService.create(film).getId();
        final User user = getRandomUser();
        final Long userId = userService.create(user).getId();

        filmService.addLike(filmId, userId);

        final Film savedFilm = filmStorage.findById(filmId).orElseThrow();
        final User savedUser = userStorage.findById(userId).orElseThrow();
        assertAll("Error while adding like to film",
                () -> assertEquals(1, savedFilm.getLikes().size(), "Wrong number of likes"),
                () -> assertTrue(savedFilm.getLikes().contains(userId), "Likes list should contain id = " + userId),
                () -> assertEquals(1, savedUser.getLikedFilms().size(), "Wrong number of likes"),
                () -> assertTrue(savedUser.getLikedFilms().contains(filmId), "Likes list should contain id = " + filmId)
        );
    }

    @Test
    void shouldAddLikeWhenLikedAlready() {
        final Film film = getRandomFilm();
        final Long filmId = filmService.create(film).getId();
        final User user = getRandomUser();
        final Long userId = userService.create(user).getId();
        filmService.addLike(filmId, userId);

        filmService.addLike(filmId, userId);

        final Film savedFilm = filmStorage.findById(filmId).orElseThrow();
        final User savedUser = userStorage.findById(userId).orElseThrow();
        assertAll("Error while adding like to film",
                () -> assertEquals(1, savedFilm.getLikes().size(), "Wrong number of likes"),
                () -> assertTrue(savedFilm.getLikes().contains(userId), "Likes list should contain id = " + userId),
                () -> assertEquals(1, savedUser.getLikedFilms().size(), "Wrong number of likes"),
                () -> assertTrue(savedUser.getLikedFilms().contains(filmId), "Likes list should contain id = " + filmId)
        );
    }

    @Test
    void shouldThrowWhenDeleteLikeAndFilmNotExist() {
        final Film film = getRandomFilm();
        final Long filmId = filmService.create(film).getId();
        final User user = getRandomUser();
        final Long userId = userService.create(user).getId();
        final Long wrongId = filmId + 1;

        Exception exception = assertThrows(NotFoundException.class, () -> filmService.deleteLike(wrongId, userId));
        assertEquals("Cannot find model 'film' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldThrowWhenDeleteLikeAndUserNotExist() {
        final Film film = getRandomFilm();
        final Long filmId = filmService.create(film).getId();
        final User user = getRandomUser();
        final Long userId = userService.create(user).getId();
        final Long wrongId = userId + 1;

        Exception exception = assertThrows(NotFoundException.class, () -> filmService.deleteLike(filmId, wrongId));
        assertEquals("Cannot find model 'user' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldDeleteLike() {
        final Film film = getRandomFilm();
        final Long filmId = filmService.create(film).getId();
        final Film anotherFilm = getRandomFilm();
        final Long anotherFilmId = filmService.create(anotherFilm).getId();
        final User user = getRandomUser();
        final Long userId = userService.create(user).getId();
        final User anotheruser = getRandomUser();
        final Long anotherUserId = userService.create(anotheruser).getId();
        filmService.addLike(filmId, userId);
        filmService.addLike(filmId, anotherUserId);
        filmService.addLike(anotherFilmId, userId);

        filmService.deleteLike(filmId, userId);

        final Film savedFilm = filmStorage.findById(filmId).orElseThrow();
        final User savedUser = userStorage.findById(userId).orElseThrow();
        assertAll("Error while adding like to film",
                () -> assertEquals(1, savedFilm.getLikes().size(), "Wrong number of likes"),
                () -> assertTrue(savedFilm.getLikes().contains(anotherUserId),
                        "Likes list should contain id = " + anotherUserId),
                () -> assertEquals(1, savedUser.getLikedFilms().size(), "Wrong number of likes"),
                () -> assertTrue(savedUser.getLikedFilms().contains(anotherFilmId),
                        "Likes list should contain id = " + anotherFilmId)
        );
    }

    @Test
    void shouldDeleteLikeWhenNoLikeAlready() {
        final Film film = getRandomFilm();
        final Long filmId = filmService.create(film).getId();
        final Film anotherFilm = getRandomFilm();
        final Long anotherFilmId = filmService.create(anotherFilm).getId();
        final User user = getRandomUser();
        final Long userId = userService.create(user).getId();
        final User anotheruser = getRandomUser();
        final Long anotherUserId = userService.create(anotheruser).getId();
        filmService.addLike(filmId, anotherUserId);
        filmService.addLike(anotherFilmId, userId);

        filmService.deleteLike(filmId, userId);

        final Film savedFilm = filmStorage.findById(filmId).orElseThrow();
        final User savedUser = userStorage.findById(userId).orElseThrow();
        assertAll("Error while adding like to film",
                () -> assertEquals(1, savedFilm.getLikes().size(), "Wrong number of likes"),
                () -> assertTrue(savedFilm.getLikes().contains(anotherUserId),
                        "Likes list should contain id = " + anotherUserId),
                () -> assertEquals(1, savedUser.getLikedFilms().size(), "Wrong number of likes"),
                () -> assertTrue(savedUser.getLikedFilms().contains(anotherFilmId),
                        "Likes list should contain id = " + anotherFilmId)
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {-1, 0})
    void shouldThrowWhenGetTopLikedAndCountNullOrNegativeOrZero(final Long count) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> filmService.getTopLiked(count));
        assertEquals("Count should be a positive number", exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldGetTopLiked() {
        final Film film = getRandomFilm();
        final Long filmId = filmService.create(film).getId();
        final Film anotherFilm = getRandomFilm();
        final Long anotherFilmId = filmService.create(anotherFilm).getId();
        final Film thirdFilm = getRandomFilm();
        filmService.create(thirdFilm);
        final User user = getRandomUser();
        final Long userId = userService.create(user).getId();
        final User anotheruser = getRandomUser();
        final Long anotherUserId = userService.create(anotheruser).getId();
        filmService.addLike(filmId, userId);
        filmService.addLike(anotherFilmId, anotherUserId);
        filmService.addLike(anotherFilmId, userId);

        final List<Film> films = filmService.getTopLiked(2L).stream().toList();

        assertEquals(2, films.size(), "Wrong film list size");
        assertEquals(anotherFilmId, films.getFirst().getId(), "Wrong film order");
        assertEquals(filmId, films.getLast().getId(), "Wrong film order");
    }

    @Test
    void shouldGetTopLikedWhenEmpty() {
        final Collection<Film> films = filmService.getTopLiked(2L);

        assertTrue(films.isEmpty(), "Wrong film list size");
    }
}