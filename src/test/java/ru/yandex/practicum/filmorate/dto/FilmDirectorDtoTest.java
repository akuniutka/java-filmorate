package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmDirectorDtoTest {

    private final Validator validator;

    FilmDirectorDtoTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldViolateConstraintWhenIdNull() {
        final FilmDirectorDto dto = new FilmDirectorDto();
        dto.setId(null);

        Set<ConstraintViolation<FilmDirectorDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "id".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenFilmDirectorDtoCorrect() {
        final FilmDirectorDto dto = new FilmDirectorDto();
        dto.setId(1L);

        Set<ConstraintViolation<FilmDirectorDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}