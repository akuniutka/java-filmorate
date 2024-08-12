package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.assertUserEquals;
import static ru.yandex.practicum.filmorate.TestModels.assertUserListEquals;
import static ru.yandex.practicum.filmorate.TestModels.cloneUser;
import static ru.yandex.practicum.filmorate.TestModels.getRandomUser;

public abstract class AbstractUserStorageTest {

    protected UserStorage userStorage;

    @AfterEach
    void tearDown() {
        userStorage.deleteAll();
    }

    @Test
    void shouldReturnCorrectUserList() {
        final List<User> expectedUsers = preloadData();

        final List<User> actualUsers = new ArrayList<>(userStorage.findAll());

        assertUserListEquals(expectedUsers, actualUsers);
    }

    @Test
    void shouldReturnCorrectUserById() {
        final List<User> expectedUsers = preloadData();

        final Optional<User> actual = userStorage.findById(expectedUsers.get(1).getId());

        assertTrue(actual.isPresent(), "optional should not be empty");
        assertUserEquals(expectedUsers.get(1), actual.get());
    }

    @Test
    void shouldReturnEmptyOptionalForUnknownId() {
        final List<User> expectedUsers = preloadData();
        final Long id = expectedUsers.getFirst().getId() - 1;

        final Optional<User> actual = userStorage.findById(id);

        assertTrue(actual.isEmpty(), "should find no user for id = " + id);
    }

    @Test
    void shouldUpdateUser() {
        final List<User> expectedUsers = preloadData();
        final Long userId = expectedUsers.get(1).getId();
        final User expectedUser = getRandomUser();
        expectedUser.setId(userId);

        final User savedUser = userStorage.update(cloneUser(expectedUser)).orElseThrow();
        final User actualUser = userStorage.findById(userId).orElseThrow();

        assertUserEquals(expectedUser, savedUser);
        assertUserEquals(expectedUser, actualUser);
    }

    @Test
    void shouldReturnEmptyOptionalWhenUpdateUnknownUser() {
        final List<User> expectedUsers = preloadData();
        final User expectedUser = getRandomUser();
        expectedUser.setId(expectedUsers.getFirst().getId() - 1);

        final Optional<User> savedUser = userStorage.update(cloneUser(expectedUser));

        assertTrue(savedUser.isEmpty(), "should find no user to update with id = " + expectedUser.getId());
    }

    @Test
    void shouldDeleteUser() {
        final List<User> expectedUsers = preloadData();
        final Long userId = expectedUsers.get(1).getId();
        expectedUsers.remove(1);

        userStorage.delete(userId);
        final List<User> actualUsers = new ArrayList<>(userStorage.findAll());

        assertUserListEquals(expectedUsers, actualUsers);
    }

    @Test
    void shouldDeleteAllUsers() {
        preloadData();

        userStorage.deleteAll();
        final Collection<User> actual = userStorage.findAll();

        assertTrue(actual.isEmpty(), "should no user remain");
    }

    protected List<User> preloadData() {
        final List<User> expectedUsers = new ArrayList<>();
        final List<User> savedUsers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = getRandomUser();
            User savedUser = userStorage.save(cloneUser(user));
            user.setId(savedUser.getId());
            expectedUsers.add(user);
            savedUsers.add(savedUser);
        }
        assertUserListEquals(expectedUsers, savedUsers);
        return expectedUsers;
    }
}
