package ru.yandex.practicum.filmorate.dto;

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
import static ru.yandex.practicum.filmorate.TestModels.getRandomNewFilmDto;

class NewFilmDtoTest {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final Validator validator;

    NewFilmDtoTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldViolateConstraintWhenNameNullOrBlank(final String name) {
        final NewFilmDto dto = getRandomNewFilmDto();
        dto.setName(name);

        Set<ConstraintViolation<NewFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenDescriptionExceeds200Characters() {
        final NewFilmDto dto = getRandomNewFilmDto();
        dto.setDescription(faker.lorem().characters(201, true, true));

        Set<ConstraintViolation<NewFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "description".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullSource
    void shouldViolateConstraintWhenReleaseDateNull(final LocalDate releaseDate) {
        final NewFilmDto dto = getRandomNewFilmDto();
        dto.setReleaseDate(releaseDate);

        Set<ConstraintViolation<NewFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "releaseDate".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenReleaseDateBeforeCinemaBirthday() {
        final NewFilmDto dto = getRandomNewFilmDto();
        dto.setReleaseDate(CINEMA_BIRTHDAY.minusDays(1));

        Set<ConstraintViolation<NewFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "releaseDate".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0})
    void shouldViolateConstraintWhenDurationNullOrZeroOrNegative(final Integer duration) {
        final NewFilmDto dto = getRandomNewFilmDto();
        dto.setDuration(duration);

        Set<ConstraintViolation<NewFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "duration".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenNewFilmDtoCorrect() {
        final NewFilmDto dto = getRandomNewFilmDto();

        Set<ConstraintViolation<NewFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldViolateNoConstraintWhenDescriptionNullOrBlank(final String description) {
        final NewFilmDto dto = getRandomNewFilmDto();
        dto.setDescription(description);

        Set<ConstraintViolation<NewFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldViolateNoConstraintWhenReleaseDateEqualsCinemaBirthday() {
        final NewFilmDto dto = getRandomNewFilmDto();
        dto.setReleaseDate(CINEMA_BIRTHDAY);

        Set<ConstraintViolation<NewFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldViolateNoConstraintWhenDuration1Minute() {
        final NewFilmDto dto = getRandomNewFilmDto();
        dto.setDuration(1);

        Set<ConstraintViolation<NewFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}