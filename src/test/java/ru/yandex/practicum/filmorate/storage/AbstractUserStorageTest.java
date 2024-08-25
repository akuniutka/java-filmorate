package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        final long id = expectedUsers.getFirst().getId() - 1;

        final Optional<User> actual = userStorage.findById(id);

        assertTrue(actual.isEmpty(), "should find no user for id = " + id);
    }

    @Test
    void shouldUpdateUser() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.get(1).getId();
        final User expectedUser = getRandomUser();
        expectedUser.setId(id);

        final User savedUser = userStorage.update(cloneUser(expectedUser)).orElseThrow();
        final User actualUser = userStorage.findById(id).orElseThrow();

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
    void shouldThrowWhenAddFriendAndUserNotExist() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.getFirst().getId() - 1;
        final long friendId = expectedUsers.getLast().getId();

        assertThrows(RuntimeException.class, () -> userStorage.addFriend(id, friendId));
    }

    @Test
    void shouldThrowWhenAddFriendAndFriendNotExist() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.getFirst().getId();
        final long friendId = expectedUsers.getLast().getId() + 1;

        assertThrows(RuntimeException.class, () -> userStorage.addFriend(id, friendId));
    }

    @Test
    void shouldThrowWhenAddFriendAndFriendIsUserThemself() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.getFirst().getId();

        assertThrows(RuntimeException.class, () -> userStorage.addFriend(id, id));
    }

    @Test
    void shouldAddFriend() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.getFirst().getId();
        final long friendId = expectedUsers.getLast().getId();
        final List<User> expectedFriends = List.of(expectedUsers.getLast());

        userStorage.addFriend(id, friendId);
        final List<User> actualFriends = new ArrayList<>(userStorage.findFriends(id));
        final List<User> friendFriends = new ArrayList<>(userStorage.findFriends(friendId));

        assertUserListEquals(expectedFriends, actualFriends);
        assertTrue(friendFriends.isEmpty(), "friends list of friend should remain empty");
    }

    @Test
    void shouldNotThrowWhenAddFriendWhichAlreadyFriend() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.getFirst().getId();
        final long friendId = expectedUsers.getLast().getId();
        final List<User> expectedFriends = List.of(expectedUsers.getLast());

        userStorage.addFriend(id, friendId);
        assertDoesNotThrow(() -> userStorage.addFriend(id, friendId));
        final List<User> actualFriends = new ArrayList<>(userStorage.findFriends(id));
        final List<User> friendFriends = new ArrayList<>(userStorage.findFriends(friendId));

        assertUserListEquals(expectedFriends, actualFriends);
        assertTrue(friendFriends.isEmpty(), "friends list of friend should remain empty");
    }

    @Test
    void shouldDeleteFriend() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.getFirst().getId();
        final long friendId = expectedUsers.getLast().getId();

        userStorage.addFriend(id, friendId);
        userStorage.deleteFriend(id, friendId);
        final List<User> actualFriends = new ArrayList<>(userStorage.findFriends(id));

        assertTrue(actualFriends.isEmpty(), "friends list should be empty");
    }

    @Test
    void shouldNotThrowWhenDeleteFriendAndNotFriend() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.getFirst().getId();
        final long friendId = expectedUsers.getLast().getId();

        assertDoesNotThrow(() -> userStorage.deleteFriend(id, friendId));
    }

    @Test
    void shouldNotThrowWhenDeleteFriendAndUserNotExist() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.getFirst().getId();
        final long friendId = expectedUsers.getLast().getId();
        final long nonExistingUserId = id - 1;

        userStorage.addFriend(id, friendId);
        assertDoesNotThrow(() -> userStorage.deleteFriend(nonExistingUserId, friendId));
    }

    @Test
    void shouldNotThrowWhenDeleteFriendAndFriendNotExist() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.getFirst().getId();
        final long friendId = expectedUsers.getLast().getId();
        final long nonExistingUserId = friendId + 1;

        userStorage.addFriend(id, friendId);
        assertDoesNotThrow(() -> userStorage.deleteFriend(id, nonExistingUserId));
    }

    @Test
    void shouldReturnCorrectCommonFriendsList() {
        final List<User> expectedUsers = preloadData();
        final long id = expectedUsers.getFirst().getId();
        final long friendId = expectedUsers.getLast().getId();
        final long commonFriendId = expectedUsers.get(1).getId();
        final List<User> expectedFriends = List.of(expectedUsers.get(1));

        userStorage.addFriend(id, commonFriendId);
        userStorage.addFriend(friendId, commonFriendId);
        final List<User> actualFriends = new ArrayList<>(userStorage.findCommonFriends(id, friendId));

        assertUserListEquals(expectedFriends, actualFriends);
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
