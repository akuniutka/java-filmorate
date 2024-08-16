package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmMpaTest {

    private final Validator validator;

    FilmMpaTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldViolateConstraintWhenIdNull() {
        final FilmMpa dto = new FilmMpa();
        dto.setId(null);

        Set<ConstraintViolation<FilmMpa>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "id".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenFilmMpaCorrect() {
        final FilmMpa dto = new FilmMpa();
        dto.setId(1L);

        Set<ConstraintViolation<FilmMpa>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}