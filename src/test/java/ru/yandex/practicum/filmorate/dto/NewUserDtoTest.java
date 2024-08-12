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
import static ru.yandex.practicum.filmorate.TestModels.getRandomNewUserDto;

class NewUserDtoTest {

    private final Validator validator;

    NewUserDtoTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "test"})
    void shouldViolateConstraintWhenEmailNullOrBlankOrMalformed(final String email) {
        final NewUserDto dto = getRandomNewUserDto();
        dto.setEmail(email);

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "email".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "super admin"})
    void shouldViolateConstraintWhenLoginNullOrBlankOrContainsWhitespace(final String login) {
        final NewUserDto dto = getRandomNewUserDto();
        dto.setLogin(login);

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "login".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullSource
    void shouldViolateConstraintWhenBirthdayNull(final LocalDate birthday) {
        final NewUserDto dto = getRandomNewUserDto();
        dto.setBirthday(birthday);

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "birthday".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenBirthdayFuture() {
        final NewUserDto dto = getRandomNewUserDto();
        dto.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(dto);

        assertTrue(violations.stream().anyMatch(v -> "birthday".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenNewUserDtoCorrect() {
        final NewUserDto dto = getRandomNewUserDto();

        Set<ConstraintViolation<NewUserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}