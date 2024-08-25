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

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.TestModels.getRandomNewDirectorDto;

class NewDirectorDtoTest {

    private final Validator validator;

    NewDirectorDtoTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldViolateConstraintWhenNameNullOrBlank(final String name) {
        final NewDirectorDto dto = getRandomNewDirectorDto();
        dto.setName(name);

        Set<ConstraintViolation<NewDirectorDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "name".endsWith(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenNewDirectorDtoCorrect() {
        final NewDirectorDto dto = getRandomNewDirectorDto();

        Set<ConstraintViolation<NewDirectorDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}