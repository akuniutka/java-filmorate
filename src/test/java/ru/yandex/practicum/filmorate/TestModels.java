package ru.yandex.practicum.filmorate;

import com.github.javafaker.Faker;
import ru.yandex.practicum.filmorate.dto.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class TestModels {

    public static final Faker faker = new Faker();

    private TestModels() {
    }

    public static User getRandomUser() {
        final User user = new User();
        user.setEmail(faker.internet().emailAddress());
        user.setLogin(faker.name().username());
        user.setName(faker.name().fullName());
        user.setBirthday(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        return user;
    }

    public static NewUserDto getRandomNewUserDto() {
        final NewUserDto dto = new NewUserDto();
        dto.setEmail(faker.internet().emailAddress());
        dto.setLogin(faker.name().username());
        dto.setName(faker.name().fullName());
        dto.setBirthday(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        return dto;
    }

    public static UpdateUserDto getRandomUpdateUserDto() {
        final UpdateUserDto dto = new UpdateUserDto();
        dto.setId(faker.number().randomNumber());
        dto.setEmail(faker.internet().emailAddress());
        dto.setLogin(faker.name().username());
        dto.setName(faker.name().fullName());
        dto.setBirthday(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        return dto;
    }

    public static Film getRandomFilm() {
        final Film film = new Film();
        film.setName(faker.book().title());
        film.setDescription(faker.lorem().characters(200, true, true));
        film.setReleaseDate(faker.date().between(
                Date.from(LocalDate.of(1895, 12, 28).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        ).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        film.setDuration(faker.number().numberBetween(1, Integer.MAX_VALUE));
        film.setGenres(Collections.emptySet());
        return film;
    }

    public static NewFilmDto getRandomNewFilmDto() {
        final NewFilmDto dto = new NewFilmDto();
        dto.setName(faker.book().title());
        dto.setDescription(faker.lorem().characters(200, true, true));
        dto.setReleaseDate(faker.date().between(
                Date.from(LocalDate.of(1895, 12, 28).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        ).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        dto.setDuration(faker.number().numberBetween(1, Integer.MAX_VALUE));
        return dto;
    }

    public static UpdateFilmDto getRandomUpdateFilmDto() {
        final UpdateFilmDto dto = new UpdateFilmDto();
        dto.setId(faker.number().randomNumber());
        dto.setName(faker.book().title());
        dto.setDescription(faker.lorem().characters(200, true, true));
        dto.setReleaseDate(faker.date().between(
                Date.from(LocalDate.of(1895, 12, 28).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        ).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        dto.setDuration(faker.number().numberBetween(1, Integer.MAX_VALUE));
        return dto;
    }

    public static Director getRandomDirector() {
        final Director director = new Director();
        director.setName(faker.name().fullName());
        return director;
    }

    public static NewDirectorDto getRandomNewDirectorDto() {
        final NewDirectorDto dto = new NewDirectorDto();
        dto.setName(faker.name().fullName());
        return dto;
    }

    public static UpdateDirectorDto getRandomUpdateDirectorDto() {
        final UpdateDirectorDto dto = new UpdateDirectorDto();
        dto.setId(faker.number().randomNumber());
        dto.setName(faker.name().fullName());
        return dto;
    }

    public static User cloneUser(final User user) {
        final User clone = new User();
        clone.setId(user.getId());
        clone.setEmail(user.getEmail());
        clone.setLogin(user.getLogin());
        clone.setName(user.getName());
        clone.setBirthday(user.getBirthday());
        return clone;
    }

    public static Film cloneFilm(final Film film) {
        final Film clone = new Film();
        clone.setId(film.getId());
        clone.setName(film.getName());
        clone.setDescription(film.getDescription());
        clone.setReleaseDate(film.getReleaseDate());
        clone.setDuration(film.getDuration());
        if (film.getMpa() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(film.getMpa().getId());
            mpa.setName(film.getMpa().getName());
            clone.setMpa(mpa);
        }
        if (film.getGenres() != null) {
            clone.setGenres(new HashSet<>(film.getGenres()));
        }
        return clone;
    }

    public static Director cloneDirector(final Director director) {
        final Director clone = new Director();
        clone.setId(director.getId());
        clone.setName(director.getName());
        return clone;
    }

    public static void assertUserEquals(final User expected, final User actual) {
        if (expected == null) {
            throw new IllegalArgumentException("value to check against should not be null");
        }
        assertNotNull(actual, "should be not null");
        assertAll("wrong user",
                () -> assertEquals(expected.getId(), actual.getId(), "wrong user id"),
                () -> assertEquals(expected.getEmail(), actual.getEmail(), "wrong user email"),
                () -> assertEquals(expected.getLogin(), actual.getLogin(), "wrong user login"),
                () -> assertEquals(expected.getName(), actual.getName(), "wrong user name"),
                () -> assertEquals(expected.getBirthday(), actual.getBirthday(), "wrong user birthday")
        );
    }

    public static void assertUserListEquals(final List<User> expected, final List<User> actual) {
        if (expected == null) {
            throw new IllegalArgumentException("value to check against should not be null");
        }
        assertNotNull(actual, "should be not null");
        assertEquals(expected.size(), actual.size(), "wrong list size");
        for (int i = 0; i < expected.size(); i++) {
            assertUserEquals(expected.get(i), actual.get(i));
        }
    }

    public static void assertFilmEquals(final Film expected, final Film actual) {
        if (expected == null) {
            throw new IllegalArgumentException("value to check against should not be null");
        }
        assertNotNull(actual, "should be not null");
        assertAll("wrong user",
                () -> assertEquals(expected.getId(), actual.getId(), "wrong film id"),
                () -> assertEquals(expected.getName(), actual.getName(), "wrong film name"),
                () -> assertEquals(expected.getDescription(), actual.getDescription(), "wrong film description"),
                () -> assertEquals(expected.getReleaseDate(), actual.getReleaseDate(), "wrong film release date"),
                () -> assertEquals(expected.getDuration(), actual.getDuration(), "wrong film duration"),
                () -> assertEquals(expected.getMpa(), actual.getMpa(), "wrong film MPA"),
                () -> assertGenreListEquals(expected.getGenres(), actual.getGenres(), "wrong film genres")
        );
    }

    public static void assertFilmListEquals(final List<Film> expected, final List<Film> actual) {
        if (expected == null) {
            throw new IllegalArgumentException("value to check against should not be null");
        }
        assertNotNull(actual, "should be not null");
        assertEquals(expected.size(), actual.size(), "wrong list size");
        for (int i = 0; i < expected.size(); i++) {
            assertFilmEquals(expected.get(i), actual.get(i));
        }
    }

    public static void assertGenreListEquals(final Collection<Genre> expected, final Collection<Genre> actual,
            String message
    ) {
        if (expected == null) {
            throw new IllegalArgumentException("value to check against should not be null");
        }
        assertNotNull(actual, "should be not null");
        assertEquals(new ArrayList<>(expected), new ArrayList<>(actual), message);
    }

    public static void assertDirectorEquals(final Director expected, final Director actual) {
        if (expected == null) {
            throw new IllegalArgumentException("value to check against should not be null");
        }
        assertNotNull(actual, "should be not null");
        assertAll("wrong user",
                () -> assertEquals(expected.getId(), actual.getId(), "wrong director id"),
                () -> assertEquals(expected.getName(), actual.getName(), "wrong director name")
        );
    }

    public static void assertDirectorListEquals(final List<Director> expected, final List<Director> actual) {
        if (expected == null) {
            throw new IllegalArgumentException("value to check against should not be null");
        }
        assertNotNull(actual, "should be not null");
        assertEquals(expected.size(), actual.size(), "wrong list size");
        for (int i = 0; i < expected.size(); i++) {
            assertDirectorEquals(expected.get(i), actual.get(i));
        }
    }
}
