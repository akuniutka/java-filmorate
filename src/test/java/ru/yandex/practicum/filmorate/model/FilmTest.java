package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.faker;
import static ru.yandex.practicum.filmorate.TestModels.getRandomFilm;

class FilmTest {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final Validator validator;

    FilmTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldViolateConstraintWhenNameNullOrBlank(String name) {
        final Film film = getRandomFilm();
        film.setName(name);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenDescriptionExceeds200Characters() {
        final Film film = getRandomFilm();
        film.setDescription(faker.lorem().characters(201, true, true));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(v -> "description".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullSource
    void shouldViolateConstraintWhenReleaseDateNull(LocalDate releaseDate) {
        final Film film = getRandomFilm();
        film.setReleaseDate(releaseDate);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(v -> "releaseDate".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenReleaseDateBeforeCinemaBirthday() {
        final Film film = getRandomFilm();
        film.setReleaseDate(CINEMA_BIRTHDAY.minusDays(1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(v -> "releaseDate".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0})
    void shouldViolateConstraintWhenDurationNullOrZeroOrNegative(Integer duration) {
        final Film film = getRandomFilm();
        film.setDuration(duration);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.stream().anyMatch(v -> "duration".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenFilmCorrect() {
        final Film film = getRandomFilm();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldViolateNoConstraintWhenDescriptionNullOrBlank(String description) {
        final Film film = getRandomFilm();
        film.setDescription(description);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldViolateNoConstraintWhenReleaseDateEqualsCinemaBirthday() {
        final Film film = getRandomFilm();
        film.setReleaseDate(CINEMA_BIRTHDAY);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldViolateNoConstraintWhenDuration1Minute() {
        final Film film = getRandomFilm();
        film.setDuration(1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }
}