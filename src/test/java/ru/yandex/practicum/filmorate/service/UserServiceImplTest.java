package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserFriendToThemselfException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
        this.userStorage = new InMemoryUserStorage();
        this.userService = new UserServiceImpl(userStorage);
    }

    @Test
    void shouldThrowsWhenCreateAndUserNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> userService.create(null));
        assertEquals("Cannot create user: is null", exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldReturnUserWithIdAssignedWhenCreate() {
        final User user = getRandomUser();
        final String email = user.getEmail();
        final String login = user.getLogin();
        final String name = user.getName();
        final LocalDate birthday = user.getBirthday();

        final User createdUser = userService.create(user);

        final User savedUser = userStorage.findById(createdUser.getId()).orElseThrow();
        assertAll("User created with errors",
                () -> assertNotNull(createdUser.getId(), "User ID should be not null"),
                () -> assertEquals(email, createdUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, createdUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, createdUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, createdUser.getBirthday(), "Wrong birthday"),
                () -> assertTrue(createdUser.getFriends().isEmpty(), "Wrong list of friends"),
                () -> assertTrue(createdUser.getLikedFilms().isEmpty(), "Wrong list of liked films"),
                () -> assertEquals(createdUser.getId(), savedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday"),
                () -> assertTrue(savedUser.getFriends().isEmpty(), "Wrong list of friends"),
                () -> assertTrue(savedUser.getLikedFilms().isEmpty(), "Wrong list of liked films")
        );
    }

    @Test
    void shouldReturnUserWithIdAssignedWhenCreateAndIdNotNull() {
        final User user = getRandomUser();
        final Long userId = userService.create(user).getId();
        final User otherUser = getRandomUser();
        otherUser.setId(userId);
        final String email = otherUser.getEmail();
        final String login = otherUser.getLogin();
        final String name = otherUser.getName();
        final LocalDate birthday = otherUser.getBirthday();

        final User createdUser = userService.create(otherUser);

        final User savedUser = userStorage.findById(createdUser.getId()).orElseThrow();
        assertAll("User created with errors",
                () -> assertNotEquals(userId, createdUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, createdUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, createdUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, createdUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, createdUser.getBirthday(), "Wrong birthday"),
                () -> assertTrue(createdUser.getFriends().isEmpty(), "Wrong list of friends"),
                () -> assertTrue(createdUser.getLikedFilms().isEmpty(), "Wrong list of liked films"),
                () -> assertEquals(createdUser.getId(), savedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday"),
                () -> assertTrue(savedUser.getFriends().isEmpty(), "Wrong list of friends"),
                () -> assertTrue(savedUser.getLikedFilms().isEmpty(), "Wrong list of liked films")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldSetNameEqualToLoginWhenCreateAndNameNullOrBlank(final String name) {
        final User user = getRandomUser();
        user.setName(name);
        final String email = user.getEmail();
        final String login = user.getLogin();
        final LocalDate birthday = user.getBirthday();

        final User createdUser = userService.create(user);

        final User savedUser = userStorage.findById(createdUser.getId()).orElseThrow();
        assertAll("User created with errors",
                () -> assertNotNull(createdUser.getId(), "User ID should be not null"),
                () -> assertEquals(email, createdUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, createdUser.getLogin(), "Wrong login"),
                () -> assertEquals(login, createdUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, createdUser.getBirthday(), "Wrong birthday"),
                () -> assertTrue(createdUser.getFriends().isEmpty(), "Wrong list of friends"),
                () -> assertTrue(createdUser.getLikedFilms().isEmpty(), "Wrong list of liked films"),
                () -> assertEquals(createdUser.getId(), savedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(login, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday"),
                () -> assertTrue(savedUser.getFriends().isEmpty(), "Wrong list of friends"),
                () -> assertTrue(savedUser.getLikedFilms().isEmpty(), "Wrong list of liked films")
        );
    }

    @Test
    void shouldReturnUsersWhenFindAll() {
        final User user = getRandomUser();
        user.setId(faker.number().randomNumber());
        userStorage.save(user);
        final Collection<User> expectedUsers = List.of(user);

        final Collection<User> users = userService.findAll();

        assertEquals(expectedUsers.size(), users.size(), "Wrong users list");
        assertTrue(users.containsAll(expectedUsers), "Wrong users list");
    }

    @Test
    void shouldThrowWhenUpdateAndUserNull() {
        Exception exception = assertThrows(NullPointerException.class, () -> userService.update(null));
        assertEquals("Cannot update user: is null", exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldThrowWhenUpdateAndUserNotExist() {
        final User newUser = getRandomUser();
        final Long userId = faker.number().randomNumber();
        newUser.setId(userId);

        Exception exception = assertThrows(NotFoundException.class, () -> userService.update(newUser));
        assertEquals("Cannot find model 'user' with id = " + userId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldReturnUpdatedUserWhenUpdate() {
        final User oldUser = getRandomUser();
        final Long userId = faker.number().randomNumber();
        final Set<Long> friends = Set.of(faker.number().randomNumber());
        final Set<Long> likedFilms = Set.of(faker.number().randomNumber());
        oldUser.setId(userId);
        oldUser.setFriends(friends);
        oldUser.setLikedFilms(likedFilms);
        userStorage.save(oldUser);
        final User newUser = getRandomUser();
        newUser.setId(userId);
        final String email = newUser.getEmail();
        final String login = newUser.getLogin();
        final String name = newUser.getName();
        final LocalDate birthday = newUser.getBirthday();

        final User updatedUser = userService.update(newUser);

        final User savedUser = userStorage.findById(userId).orElseThrow();
        assertAll("User updated with errors",
                () -> assertEquals(userId, updatedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, updatedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, updatedUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, updatedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, updatedUser.getBirthday(), "Wrong birthday"),
                () -> assertEquals(friends.size(), updatedUser.getFriends().size(), "Wrong list of friends"),
                () -> assertTrue(updatedUser.getFriends().containsAll(friends), "Wrong list of friends"),
                () -> assertEquals(likedFilms.size(), updatedUser.getLikedFilms().size(), "Wrong list of liked films"),
                () -> assertTrue(updatedUser.getLikedFilms().containsAll(likedFilms), "Wrong list of liked films"),
                () -> assertEquals(userId, savedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday"),
                () -> assertEquals(friends.size(), savedUser.getFriends().size(), "Wrong list of friends"),
                () -> assertTrue(savedUser.getFriends().containsAll(friends), "Wrong list of friends"),
                () -> assertEquals(likedFilms.size(), savedUser.getLikedFilms().size(), "Wrong list of liked films"),
                () -> assertTrue(savedUser.getLikedFilms().containsAll(likedFilms), "Wrong list of liked films")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldSetNameEqualTLoginWhenUpdateAndNameNullOrBlank(final String name) {
        final User oldUser = getRandomUser();
        final Long userId = faker.number().randomNumber();
        final Set<Long> friends = Set.of(faker.number().randomNumber());
        final Set<Long> likedFilms = Set.of(faker.number().randomNumber());
        oldUser.setId(userId);
        oldUser.setFriends(friends);
        oldUser.setLikedFilms(likedFilms);
        userStorage.save(oldUser);
        final User newUser = getRandomUser();
        newUser.setId(userId);
        newUser.setName(name);
        final String email = newUser.getEmail();
        final String login = newUser.getLogin();
        final LocalDate birthday = newUser.getBirthday();

        final User updatedUser = userService.update(newUser);

        final User savedUser = userStorage.findById(userId).orElseThrow();
        assertAll("User updated with errors",
                () -> assertEquals(userId, updatedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, updatedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, updatedUser.getLogin(), "Wrong login"),
                () -> assertEquals(login, updatedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, updatedUser.getBirthday(), "Wrong birthday"),
                () -> assertEquals(friends.size(), updatedUser.getFriends().size(), "Wrong list of friends"),
                () -> assertTrue(updatedUser.getFriends().containsAll(friends), "Wrong list of friends"),
                () -> assertEquals(likedFilms.size(), updatedUser.getLikedFilms().size(), "Wrong list of liked films"),
                () -> assertTrue(updatedUser.getLikedFilms().containsAll(likedFilms), "Wrong list of liked films"),
                () -> assertEquals(userId, savedUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, savedUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, savedUser.getLogin(), "Wrong login"),
                () -> assertEquals(login, savedUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, savedUser.getBirthday(), "Wrong birthday"),
                () -> assertEquals(friends.size(), savedUser.getFriends().size(), "Wrong list of friends"),
                () -> assertTrue(savedUser.getFriends().containsAll(friends), "Wrong list of friends"),
                () -> assertEquals(likedFilms.size(), savedUser.getLikedFilms().size(), "Wrong list of liked films"),
                () -> assertTrue(savedUser.getLikedFilms().containsAll(likedFilms), "Wrong list of liked films")
        );
    }

    @Test
    void shouldThrowWhenFindUserByIdAndUserNotExist() {
        final Long wrongId = faker.number().randomNumber();

        Exception exception = assertThrows(NotFoundException.class, () -> userService.findUserById(wrongId));
        assertEquals("Cannot find model 'user' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldFindUserById() {
        final User user = getRandomUser();
        final String email = user.getEmail();
        final String login = user.getLogin();
        final String name = user.getName();
        final LocalDate birthday = user.getBirthday();
        final Long userId = userService.create(user).getId();

        final User foundUser = userService.findUserById(userId);

        assertAll("User retrieved with errors",
                () -> assertEquals(userId, foundUser.getId(), "Wrong user ID"),
                () -> assertEquals(email, foundUser.getEmail(), "Wrong email"),
                () -> assertEquals(login, foundUser.getLogin(), "Wrong login"),
                () -> assertEquals(name, foundUser.getName(), "Wrong name"),
                () -> assertEquals(birthday, foundUser.getBirthday(), "Wrong birthday")
        );
    }

    @Test
    void shouldThrowWhenAddFriendAndUserNotExist() {
        final User alice = getRandomUser();
        userService.create(alice);
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();
        final Long wrongId = bobId + 1;

        Exception exception = assertThrows(NotFoundException.class, () -> userService.addFriend(wrongId, bobId));
        assertEquals("Cannot find model 'user' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldThrowWhenAddFriendAndFriendNotExist() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();
        final Long wrongId = bobId + 1;

        Exception exception = assertThrows(NotFoundException.class, () -> userService.addFriend(aliceId, wrongId));
        assertEquals("Cannot find model 'user' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldThrowWhenAddFriendAndFriendIsUserThemself() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();

        Exception exception = assertThrows(UserFriendToThemselfException.class,
                () -> userService.addFriend(aliceId, aliceId));
        assertEquals("User cannot be friend to themself (id = %d)".formatted(aliceId), exception.getMessage(),
                WRONG_MESSAGE);
    }

    @Test
    void shouldAddFriend() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();

        userService.addFriend(aliceId, bobId);

        final User savedAlice = userStorage.findById(aliceId).orElseThrow();
        final User savedBob = userStorage.findById(bobId).orElseThrow();
        assertAll("Error while making users friends",
                () -> assertEquals(1, savedAlice.getFriends().size(), "Wrong number of friends"),
                () -> assertTrue(savedAlice.getFriends().contains(bobId), "Friends list should contain id = " + bobId),
                () -> assertEquals(1, savedBob.getFriends().size(), "Wrong number of friends"),
                () -> assertTrue(savedBob.getFriends().contains(aliceId), "Friend list should contain id = " + aliceId)
        );
    }

    @Test
    void shouldAddFriendWhenFriendAlready() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();
        userService.addFriend(aliceId, bobId);

        userService.addFriend(aliceId, bobId);

        final User savedAlice = userStorage.findById(aliceId).orElseThrow();
        final User savedBob = userStorage.findById(bobId).orElseThrow();
        assertAll("Error while making users friends",
                () -> assertEquals(1, savedAlice.getFriends().size(), "Wrong number of friends"),
                () -> assertTrue(savedAlice.getFriends().contains(bobId), "Friends list should contain id = " + bobId),
                () -> assertEquals(1, savedBob.getFriends().size(), "Wrong number of friends"),
                () -> assertTrue(savedBob.getFriends().contains(aliceId), "Friend list should contain id = " + aliceId)
        );
    }

    @Test
    void shouldThrowWhenDeleteFriendAndUserNotExist() {
        final User alice = getRandomUser();
        userService.create(alice);
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();
        final Long wrongId = bobId + 1;

        Exception exception = assertThrows(NotFoundException.class, () -> userService.deleteFriend(wrongId, bobId));
        assertEquals("Cannot find model 'user' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldThrowWhenDeleteFriendAndFriendNotExist() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();
        final Long wrongId = bobId + 1;

        Exception exception = assertThrows(NotFoundException.class, () -> userService.deleteFriend(aliceId, wrongId));
        assertEquals("Cannot find model 'user' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldDeleteFriend() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();
        final User charlie = getRandomUser();
        final Long charlieId = userService.create(charlie).getId();
        userService.addFriend(aliceId, bobId);
        userService.addFriend(aliceId, charlieId);
        userService.addFriend(bobId, charlieId);

        userService.deleteFriend(aliceId, bobId);

        final User savedAlice = userStorage.findById(aliceId).orElseThrow();
        final User savedBob = userStorage.findById(bobId).orElseThrow();
        assertAll("Error while removing friend",
                () -> assertEquals(1, savedAlice.getFriends().size(), "Wrong number of friends"),
                () -> assertTrue(savedAlice.getFriends().contains(charlieId),
                        "Friends list should contain id = " + charlieId),
                () -> assertEquals(1, savedBob.getFriends().size(), "Wrong number of friends"),
                () -> assertTrue(savedBob.getFriends().contains(charlieId),
                        "Friend list should contain id = " + charlieId)
        );
    }

    @Test
    void shouldDeleteFriendWhenNotFriendsAlready() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();
        final User charlie = getRandomUser();
        final Long charlieId = userService.create(charlie).getId();
        userService.addFriend(aliceId, charlieId);
        userService.addFriend(bobId, charlieId);

        userService.deleteFriend(aliceId, bobId);

        final User savedAlice = userStorage.findById(aliceId).orElseThrow();
        final User savedBob = userStorage.findById(bobId).orElseThrow();
        assertAll("Error while removing friend",
                () -> assertEquals(1, savedAlice.getFriends().size(), "Wrong number of friends"),
                () -> assertTrue(savedAlice.getFriends().contains(charlieId),
                        "Friends list should contain id = " + charlieId),
                () -> assertEquals(1, savedBob.getFriends().size(), "Wrong number of friends"),
                () -> assertTrue(savedBob.getFriends().contains(charlieId),
                        "Friend list should contain id = " + charlieId)
        );
    }

    @Test
    void shouldThrowWhenFindFriendsByUserIdAndUserNotExist() {
        final Long wrongId = faker.number().randomNumber();

        Exception exception = assertThrows(NotFoundException.class, () -> userService.findFriendsByUserId(wrongId));
        assertEquals("Cannot find model 'user' with id = " + wrongId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldFindFriendsByUserId() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();
        final User charlie = getRandomUser();
        final Long charlieId = userService.create(charlie).getId();
        userService.addFriend(aliceId, bobId);
        userService.addFriend(aliceId, charlieId);
        userService.addFriend(bobId, charlieId);

        final Collection<User> friends = userService.findFriendsByUserId(aliceId);

        assertAll("Wrong list of friends",
                () -> assertEquals(2, friends.size(), "Wrong number of friends"),
                () -> assertTrue(friends.contains(bob), "Friends list does not contain " + bob),
                () -> assertTrue(friends.contains(charlie), "Friend list does not contain id = " + charlie)
        );
    }

    @Test
    void shouldFindFriendsByUserIdWhenEmpty() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();

        final Collection<User> friends = userService.findFriendsByUserId(aliceId);

        assertTrue(friends.isEmpty(), "Wrong list of friends");
    }

    @Test
    void shouldThrowWhenFindCommonFriendsByUserIdsAndUser1NotExist() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final Long bobId = aliceId + 1;

        Exception exception = assertThrows(NotFoundException.class,
                () -> userService.findCommonFriendsByUserIds(bobId, aliceId));
        assertEquals("Cannot find model 'user' with id = " + bobId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldThrowWhenFindCommonFriendsByUserIdsAndUser2NotExist() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final Long bobId = aliceId + 1;

        Exception exception = assertThrows(NotFoundException.class,
                () -> userService.findCommonFriendsByUserIds(aliceId, bobId));
        assertEquals("Cannot find model 'user' with id = " + bobId, exception.getMessage(), WRONG_MESSAGE);
    }

    @Test
    void shouldFindCommonFriendsByUserIds() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();
        final User charlie = getRandomUser();
        final Long charlieId = userService.create(charlie).getId();
        userService.addFriend(aliceId, bobId);
        userService.addFriend(aliceId, charlieId);
        userService.addFriend(bobId, charlieId);

        final Collection<User> friends = userService.findCommonFriendsByUserIds(aliceId, bobId);

        assertAll("Wrong list of friends",
                () -> assertEquals(1, friends.size(), "Wrong number of friends"),
                () -> assertTrue(friends.contains(charlie), "Friend list does not contain id = " + charlie)
        );
    }

    @Test
    void shouldFindCommonFriendsByUserIdsWhenEmpty() {
        final User alice = getRandomUser();
        final Long aliceId = userService.create(alice).getId();
        final User bob = getRandomUser();
        final Long bobId = userService.create(bob).getId();
        userService.addFriend(aliceId, bobId);

        final Collection<User> friends = userService.findCommonFriendsByUserIds(aliceId, bobId);

        assertTrue(friends.isEmpty(), "Wrong list of friends");
    }
}