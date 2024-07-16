package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserFriendToThemselfException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User create(final User user) {
        Objects.requireNonNull(user, "Cannot create user: is null");
        user.setId(null);
        resetNameToLoginIfBlank(user);
        user.setFriends(new HashSet<>());
        user.setLikedFilms(new HashSet<>());
        userStorage.save(user);
        log.info("Created new user: {}", user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User update(final User newUser) {
        Objects.requireNonNull(newUser, "Cannot update user: is null");
        final Long userId = newUser.getId();
        final User oldUser = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
        resetNameToLoginIfBlank(newUser);
        newUser.setFriends(oldUser.getFriends());
        newUser.setLikedFilms(oldUser.getLikedFilms());
        userStorage.save(newUser);
        log.info("Updated user with id = {}: {}", userId, newUser);
        return newUser;
    }

    @Override
    public User findUserById(final Long userId) {
        return userStorage.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
    }

    @Override
    public void addFriend(final Long userId, final Long friendId) {
        final User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
        final User friend = userStorage.findById(friendId).orElseThrow(() -> new NotFoundException("user", friendId));
        if (user.equals(friend)) {
            throw new UserFriendToThemselfException("User cannot be friend to themself (id = %d)".formatted(userId));
        }
        if (user.getFriends().contains(friendId) && friend.getFriends().contains(userId)) {
            log.info("Users with id = {} and id = {} are friends already", userId, friendId);
            return;
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        userStorage.save(user);
        userStorage.save(friend);
        log.info("Users with id = {} and id = {} are friends now", userId, friendId);
    }

    @Override
    public void deleteFriend(final Long userId, final Long friendId) {
        final User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
        final User friend = userStorage.findById(friendId).orElseThrow(() -> new NotFoundException("user", friendId));
        if (!user.getFriends().contains(friendId) && !friend.getFriends().contains(userId)) {
            log.info("Users with id = {} and id = {} are not friends already", userId, friendId);
            return;
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        userStorage.save(user);
        userStorage.save(friend);
        log.info("Users with id = {} and id = {} are not friends now", userId, friendId);
    }

    @Override
    public Collection<User> findFriendsByUserId(final Long userId) {
        final User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
        return user.getFriends().stream()
                .map(id -> userStorage.findById(id).orElseThrow(
                        () -> new AssertionError("Cannot retrieve friend with id = " + id))
                )
                .toList();
    }

    @Override
    public Collection<User> findCommonFriendsByUserIds(final Long userId1, final Long userId2) {
        final User user1 = userStorage.findById(userId1).orElseThrow(() -> new NotFoundException("user", userId1));
        final User user2 = userStorage.findById(userId2).orElseThrow(() -> new NotFoundException("user", userId2));
        return user1.getFriends().stream()
                .filter(id -> user2.getFriends().contains(id))
                .map(id -> userStorage.findById(id).orElseThrow(
                        () -> new AssertionError("Cannot retrieve friend with id = " + id))
                )
                .toList();
    }

    private void resetNameToLoginIfBlank(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
