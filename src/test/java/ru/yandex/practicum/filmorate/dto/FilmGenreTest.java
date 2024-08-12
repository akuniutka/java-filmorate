package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmGenreTest {

    private final Validator validator;

    FilmGenreTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldViolateConstraintWhenIdNull() {
        final FilmGenre dto = new FilmGenre();
        dto.setId(null);

        Set<ConstraintViolation<FilmGenre>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "id".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenFilmGenreCorrect() {
        final FilmGenre dto = new FilmGenre();
        dto.setId(1L);

        Set<ConstraintViolation<FilmGenre>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}