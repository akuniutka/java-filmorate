package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmMpaDtoTest {

    private final Validator validator;

    FilmMpaDtoTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldViolateConstraintWhenIdNull() {
        final FilmMpaDto dto = new FilmMpaDto();
        dto.setId(null);

        Set<ConstraintViolation<FilmMpaDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "id".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenFilmMpaDtoCorrect() {
        final FilmMpaDto dto = new FilmMpaDto();
        dto.setId(1L);

        Set<ConstraintViolation<FilmMpaDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}