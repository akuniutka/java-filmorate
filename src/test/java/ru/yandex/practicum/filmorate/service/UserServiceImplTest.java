package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.getRandomUser;

class UserServiceImplTest {

    private final UserService userService;

    public UserServiceImplTest() {
        this.userService = new UserServiceImpl(new InMemoryUserStorage());
    }

    @Test
    public void shouldReturnUserWithIdAssignedWhenCreate() {
        final User user = getRandomUser();
        final String email = user.getEmail();
        final String login = user.getLogin();
        final String name = user.getName();
        final LocalDate birthday = user.getBirthday();

        final User savedUser = userService.create(user);

        assertAll("User created with errors",
                () -> assertNotNull(savedUser.getId(), "User ID should be not null"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldSetNameEqualToLoginWhenCreateAndNameNullOrBlank(String name) {
        final User user = getRandomUser();
        user.setName(name);
        final String email = user.getEmail();
        final String login = user.getLogin();
        final LocalDate birthday = user.getBirthday();

        final User savedUser = userService.create(user);

        assertAll("User created with errors",
                () -> assertNotNull(savedUser.getId(), "User ID should be not null"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(login, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday")
        );
    }

    @Test
    public void shouldReturnUsersWhenFindAll() {
        final User user = getRandomUser();
        final Collection<User> expectedUsers = List.of(user);

        final Long userId = userService.create(user).getId();
        final Collection<User> users = userService.findAll();

        user.setId(userId);
        assertEquals(expectedUsers.size(), users.size(), "Wrong users list");
        assertTrue(users.containsAll(expectedUsers), "Wrong users list");
    }

    @Test
    public void shouldReturnUpdatedUserWhenUpdate() {
        final User oldUser = getRandomUser();
        final Long userId = userService.create(oldUser).getId();
        final User newUser = getRandomUser();
        newUser.setId(userId);

        final User savedUser = userService.update(newUser);

        assertEquals(newUser, savedUser, "User updated with errors");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldSetNameEqualTLoginWhenUpdateAndNameNullOrBlank(String name) {
        final User oldUser = getRandomUser();
        final Long userId = userService.create(oldUser).getId();
        final User newUser = getRandomUser();
        newUser.setId(userId);
        newUser.setName(name);

        final User savedUser = userService.update(newUser);

        assertEquals(newUser.getLogin(), savedUser.getName(), "Wrong name");
    }

    @Test
    public void shouldThrowWhenUpdateAndUserNotExist() {
        final User oldUser = getRandomUser();
        final Long userId = userService.create(oldUser).getId();
        final User newUser = getRandomUser();
        newUser.setId(userId + 1);

        Exception exception = assertThrows(NotFoundException.class, () -> userService.update(newUser));
        assertEquals("Cannot find model 'user' with id = " + (userId + 1), exception.getMessage(),
                "Wrong exception message");
    }
}