package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.getRandomUpdateDirectorDto;

class UpdateDirectorDtoTest {

    private final Validator validator;

    UpdateDirectorDtoTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldViolateConstraintWhenIdNull() {
        final UpdateDirectorDto dto = getRandomUpdateDirectorDto();
        dto.setId(null);

        Set<ConstraintViolation<UpdateDirectorDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "id".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldViolateConstraintWhenNameNullOrBlank(final String name) {
        final UpdateDirectorDto dto = getRandomUpdateDirectorDto();
        dto.setName(name);

        Set<ConstraintViolation<UpdateDirectorDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "name".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenNewDirectorDtoCorrect() {
        final UpdateDirectorDto dto = getRandomUpdateDirectorDto();

        Set<ConstraintViolation<UpdateDirectorDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}