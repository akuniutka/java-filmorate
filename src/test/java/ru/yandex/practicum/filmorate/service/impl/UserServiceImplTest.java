package ru.yandex.practicum.filmorate.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.mem.UserInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.faker;
import static ru.yandex.practicum.filmorate.TestModels.getRandomUser;

class UserServiceImplTest {

    private static final String WRONG_MESSAGE = "Wrong exception message";
    private final UserStorage userStorage;
    private final UserService userService;

    UserServiceImplTest() {
        this.userStorage = new UserInMemoryStorage();
        this.userService = new UserServiceImpl(userStorage);
    }

    @Test
    void shouldThrowsWhenCreateAndUserNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> userService.createUser(null));
        assertEquals("Cannot create user: is null", exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldReturnUserWithIdAssignedWhenCreateUser() {
        final User user = getRandomUser();
        final String email = user.getEmail();
        final String login = user.getLogin();
        final String name = user.getName();
        final LocalDate birthday = user.getBirthday();

        final User createdUser = userService.createUser(user);

        final User savedUser = userStorage.findById(createdUser.getId()).orElseThrow();
        assertAll("User created with errors",
                () -> assertNotNull(createdUser.getId(), "User ID should be not null"),
                () -> assertEquals(email, createdUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, createdUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, createdUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, createdUser.getBirthday(), "Wrong birthday"),
                () -> assertEquals(createdUser.getId(), savedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday")
        );
    }

    @Test
    void shouldReturnUserWithIdAssignedWhenCreateUserAndIdNotNull() {
        final User user = getRandomUser();
        final Long userId = userService.createUser(user).getId();
        final User otherUser = getRandomUser();
        otherUser.setId(userId);
        final String email = otherUser.getEmail();
        final String login = otherUser.getLogin();
        final String name = otherUser.getName();
        final LocalDate birthday = otherUser.getBirthday();

        final User createdUser = userService.createUser(otherUser);

        final User savedUser = userStorage.findById(createdUser.getId()).orElseThrow();
        assertAll("User created with errors",
                () -> assertNotEquals(userId, createdUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, createdUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, createdUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, createdUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, createdUser.getBirthday(), "Wrong birthday"),
                () -> assertEquals(createdUser.getId(), savedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldSetNameEqualToLoginWhenCreateUserAndNameNullOrBlank(final String name) {
        final User user = getRandomUser();
        user.setName(name);
        final String email = user.getEmail();
        final String login = user.getLogin();
        final LocalDate birthday = user.getBirthday();

        final User createdUser = userService.createUser(user);

        final User savedUser = userStorage.findById(createdUser.getId()).orElseThrow();
        assertAll("User created with errors",
                () -> assertNotNull(createdUser.getId(), "User ID should be not null"),
                () -> assertEquals(email, createdUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, createdUser.getLogin(), "Wrong login"),
                () -> assertEquals(login, createdUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, createdUser.getBirthday(), "Wrong birthday"),
                () -> assertEquals(createdUser.getId(), savedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(login, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday")
        );
    }

    @Test
    void shouldReturnUsersWhenGetUsers() {
        final User user = getRandomUser();
        user.setId(faker.number().randomNumber());
        userStorage.save(user);
        final Collection<User> expectedUsers = List.of(user);

        final Collection<User> users = userService.getUsers();

        assertEquals(expectedUsers.size(), users.size(), "Wrong users list");
        assertTrue(users.containsAll(expectedUsers), "Wrong users list");
    }

    @Test
    void shouldThrowWhenUpdateAndUserNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> userService.updateUser(null));
        assertEquals("Cannot update user: is null", exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldReturnEmptyOptionalWhenUpdateUserAndUserNotExist() {
        final User newUser = getRandomUser();
        final Long id = faker.number().randomNumber();
        newUser.setId(id);

        final Optional<User> actual = userService.updateUser(newUser);

        assertTrue(actual.isEmpty(), "should find no user for id = " + id);
    }

    @Test
    void shouldReturnUpdatedUserWhenUpdateUser() {
        final User oldUser = getRandomUser();
        final Long userId = userStorage.save(oldUser).getId();
        final User newUser = getRandomUser();
        newUser.setId(userId);
        final String email = newUser.getEmail();
        final String login = newUser.getLogin();
        final String name = newUser.getName();
        final LocalDate birthday = newUser.getBirthday();

        final User updatedUser = userService.updateUser(newUser).orElseThrow();

        final User savedUser = userStorage.findById(userId).orElseThrow();
        assertAll("User updated with errors",
                () -> assertEquals(userId, updatedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, updatedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, updatedUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, updatedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, updatedUser.getBirthday(), "Wrong birthday"),
                () -> assertEquals(userId, savedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldSetNameEqualTLoginWhenUpdateUserAndNameNullOrBlank(final String name) {
        final User oldUser = getRandomUser();
        final Long userId = userStorage.save(oldUser).getId();
        final User newUser = getRandomUser();
        newUser.setId(userId);
        newUser.setName(name);
        final String email = newUser.getEmail();
        final String login = newUser.getLogin();
        final LocalDate birthday = newUser.getBirthday();

        final User updatedUser = userService.updateUser(newUser).orElseThrow();

        final User savedUser = userStorage.findById(userId).orElseThrow();
        assertAll("User updated with errors",
                () -> assertEquals(userId, updatedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, updatedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, updatedUser.getLogin(), "Wrong login"),
                () -> assertEquals(login, updatedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, updatedUser.getBirthday(), "Wrong birthday"),
                () -> assertEquals(userId, savedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(login, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday")
        );
    }

    @Test
    void shouldReturnEmptyOptionalWhenGetUserAndUserNotExist() {
        final Long wrongId = faker.number().randomNumber();

        Optional<User> actual = userService.getUser(wrongId);

        assertTrue(actual.isEmpty(), "should find no user for id = " + wrongId);
    }

    @Test
    void shouldGetUser() {
        final User user = getRandomUser();
        final String email = user.getEmail();
        final String login = user.getLogin();
        final String name = user.getName();
        final LocalDate birthday = user.getBirthday();
        final Long userId = userService.createUser(user).getId();

        final User foundUser = userService.getUser(userId).orElseThrow();

        assertAll("User retrieved with errors",
                () -> assertEquals(userId, foundUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, foundUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, foundUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, foundUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, foundUser.getBirthday(), "Wrong birthday")
        );
    }
}