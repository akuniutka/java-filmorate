package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmGenreDtoTest {

    private final Validator validator;

    FilmGenreDtoTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldViolateConstraintWhenIdNull() {
        final FilmGenreDto dto = new FilmGenreDto();
        dto.setId(null);

        Set<ConstraintViolation<FilmGenreDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "id".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenFilmGenreDtoCorrect() {
        final FilmGenreDto dto = new FilmGenreDto();
        dto.setId(1L);

        Set<ConstraintViolation<FilmGenreDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}