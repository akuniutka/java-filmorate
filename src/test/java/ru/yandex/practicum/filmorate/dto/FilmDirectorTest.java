package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmDirectorTest {

    private final Validator validator;

    FilmDirectorTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldViolateConstraintWhenIdNull() {
        final FilmDirector dto = new FilmDirector();
        dto.setId(null);

        Set<ConstraintViolation<FilmDirector>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "id".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenFilmDirectorCorrect() {
        final FilmDirector dto = new FilmDirector();
        dto.setId(1L);

        Set<ConstraintViolation<FilmDirector>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}