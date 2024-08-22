package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Override
    public Collection<User> getUsers() {
        return userStorage.findAll();
    }

    @Override
    public Optional<User> getUser(final long userId) {
        return userStorage.findById(userId);
    }

    @Override
    public User createUser(final User user) {
        Objects.requireNonNull(user, "Cannot create user: is null");
        resetNameToLoginIfBlank(user);
        final User userStored = userStorage.save(user);
        log.info("Created new user: {}", userStored);

        return userStored;
    }

    @Override
    public Optional<User> updateUser(final User user) {
        Objects.requireNonNull(user, "Cannot update user: is null");
        resetNameToLoginIfBlank(user);
        final Optional<User> userStored = userStorage.update(user);
        userStored.ifPresent(u -> log.info("Updated user: {}", u));
        return userStored;
    }

    @Override
    public void addFriend(final long id, final long friendId) {
        assertUserExists(id);
        assertUserExists(friendId);
        if (Objects.equals(id, friendId)) {
            throw new ValidationException("Check that friend id is correct (you sent %s)".formatted(friendId));
        }
        userStorage.addFriend(id, friendId);
        // добавление события добавления друга в таблицу events
        Event event = new Event();
        event.setEventType(EventType.FRIEND);
        event.setUserId(id);
        event.setOperation(Operation.ADD);
        event.setTimestamp(Instant.now());
        event.setEntityId(friendId);
        eventStorage.save(event);

    }

    @Override
    public void deleteFriend(final long id, final long friendId) {
        assertUserExists(id);
        assertUserExists(friendId);
        if (Objects.equals(id, friendId)) {
            throw new ValidationException("Check that friend id is correct (you sent %s)".formatted(friendId));
        }
        userStorage.deleteFriend(id, friendId);
        // добавление события удаления друга в таблице events
        Event event = new Event();
        event.setEventType(EventType.FRIEND);
        event.setUserId(id);
        event.setOperation(Operation.REMOVE);
        event.setTimestamp(Instant.now());
        event.setEntityId(friendId);
        eventStorage.save(event);
    }

    @Override
    public Collection<User> getFriends(long id) {
        assertUserExists(id);
        return userStorage.findFriends(id);
    }

    @Override
    public Collection<User> getCommonFriends(long id, long friendId) {
        assertUserExists(id);
        assertUserExists(friendId);
        return userStorage.findCommonFriends(id, friendId);
    }

    private void assertUserExists(final long id) {
        userStorage.findById(id).orElseThrow(() -> new NotFoundException(User.class, id));
    }

    private void resetNameToLoginIfBlank(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
