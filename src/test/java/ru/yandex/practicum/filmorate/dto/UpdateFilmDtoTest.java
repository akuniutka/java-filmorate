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
import static ru.yandex.practicum.filmorate.TestModels.getRandomUpdateFilmDto;

class UpdateFilmDtoTest {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final Validator validator;

    UpdateFilmDtoTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldViolateConstraintWhenIdNull() {
        final UpdateFilmDto dto = getRandomUpdateFilmDto();
        dto.setId(null);

        Set<ConstraintViolation<UpdateFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "id".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldViolateConstraintWhenNameNullOrBlank(final String name) {
        final UpdateFilmDto dto = getRandomUpdateFilmDto();
        dto.setName(name);

        Set<ConstraintViolation<UpdateFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenDescriptionExceeds200Characters() {
        final UpdateFilmDto dto = getRandomUpdateFilmDto();
        dto.setDescription(faker.lorem().characters(201, true, true));

        Set<ConstraintViolation<UpdateFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "description".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullSource
    void shouldViolateConstraintWhenReleaseDateNull(final LocalDate releaseDate) {
        final UpdateFilmDto dto = getRandomUpdateFilmDto();
        dto.setReleaseDate(releaseDate);

        Set<ConstraintViolation<UpdateFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "releaseDate".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenReleaseDateBeforeCinemaBirthday() {
        final UpdateFilmDto dto = getRandomUpdateFilmDto();
        dto.setReleaseDate(CINEMA_BIRTHDAY.minusDays(1));

        Set<ConstraintViolation<UpdateFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "releaseDate".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0})
    void shouldViolateConstraintWhenDurationNullOrZeroOrNegative(final Integer duration) {
        final UpdateFilmDto dto = getRandomUpdateFilmDto();
        dto.setDuration(duration);

        Set<ConstraintViolation<UpdateFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "duration".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenUpdateFilmDtoCorrect() {
        final UpdateFilmDto dto = getRandomUpdateFilmDto();

        Set<ConstraintViolation<UpdateFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldViolateNoConstraintWhenDescriptionNullOrBlank(final String description) {
        final UpdateFilmDto dto = getRandomUpdateFilmDto();
        dto.setDescription(description);

        Set<ConstraintViolation<UpdateFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldViolateNoConstraintWhenReleaseDateEqualsCinemaBirthday() {
        final UpdateFilmDto dto = getRandomUpdateFilmDto();
        dto.setReleaseDate(CINEMA_BIRTHDAY);

        Set<ConstraintViolation<UpdateFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldViolateNoConstraintWhenDuration1Minute() {
        final UpdateFilmDto dto = getRandomUpdateFilmDto();
        dto.setDuration(1);

        Set<ConstraintViolation<UpdateFilmDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}