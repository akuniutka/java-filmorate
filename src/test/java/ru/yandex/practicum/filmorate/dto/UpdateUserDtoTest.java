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
import static ru.yandex.practicum.filmorate.TestModels.getRandomUpdateUserDto;

class UpdateUserDtoTest {

    private final Validator validator;

    UpdateUserDtoTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @Test
    void shouldViolateConstraintWhenIdNull() {
        final UpdateUserDto dto = getRandomUpdateUserDto();
        dto.setId(null);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "id".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "test"})
    void shouldViolateConstraintWhenEmailNullOrBlankOrMalformed(final String email) {
        final UpdateUserDto dto = getRandomUpdateUserDto();
        dto.setEmail(email);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "email".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "super admin"})
    void shouldViolateConstraintWhenLoginNullOrBlankOrContainsWhitespace(final String login) {
        final UpdateUserDto dto = getRandomUpdateUserDto();
        dto.setLogin(login);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "login".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullSource
    void shouldViolateConstraintWhenBirthdayNull(final LocalDate birthday) {
        final UpdateUserDto dto = getRandomUpdateUserDto();
        dto.setBirthday(birthday);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "birthday".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenBirthdayFuture() {
        final UpdateUserDto dto = getRandomUpdateUserDto();
        dto.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "birthday".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenNewUserDtoCorrect() {
        final UpdateUserDto dto = getRandomUpdateUserDto();

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}