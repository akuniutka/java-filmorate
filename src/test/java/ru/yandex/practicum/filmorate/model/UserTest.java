package ru.yandex.practicum.filmorate.model;

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
import static ru.yandex.practicum.filmorate.TestModels.getRandomUser;

class UserTest {

    private final Validator validator;

    UserTest() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "test"})
    void shouldViolateConstraintWhenEmailNullOrBlankOrMalformed(String email) {
        final User user = getRandomUser();
        user.setEmail(email);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(v -> "email".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "super admin"})
    void shouldViolateConstraintWhenLoginNullOrBlankOrContainsWhitespace(String login) {
        final User user = getRandomUser();
        user.setLogin(login);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(v -> "login".equals(v.getPropertyPath().toString())));
    }

    @ParameterizedTest
    @NullSource
    void shouldViolateConstraintWhenBirthdayNull(LocalDate birthday) {
        final User user = getRandomUser();
        user.setBirthday(birthday);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(v -> "birthday".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateConstraintWhenBirthdayFuture() {
        final User user = getRandomUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(v -> "birthday".equals(v.getPropertyPath().toString())));
    }

    @Test
    void shouldViolateNoConstraintWhenUserCorrect() {
        final User user = getRandomUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }
}