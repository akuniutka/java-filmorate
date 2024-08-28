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
import ru.yandex.practicum.filmorate.service.api.EventService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final EventService eventService;

    @Override
    public Collection<User> getUsers() {
        return userStorage.findAll();
    }

    @Override
    public User getUser(final long id) {
        return userStorage.findById(id).orElseThrow(
                () -> new NotFoundException(User.class, id)
        );
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
    public User updateUser(final User user) {
        Objects.requireNonNull(user, "Cannot update user: is null");
        resetNameToLoginIfBlank(user);
        final User userStored = userStorage.update(user).orElseThrow(
                () -> new NotFoundException(User.class, user.getId())
        );
        log.info("Updated user: {}", userStored);
        return userStored;
    }

    @Override
    public void addFriend(final long id, final long friendId) {
        getUser(id);
        getUser(friendId);
        if (Objects.equals(id, friendId)) {
            throw new ValidationException("Check that friend id is correct (you sent %s)".formatted(friendId));
        }
        userStorage.addFriend(id, friendId);
        // добавление события добавления друга в таблицу events
        eventService.create(EventType.FRIEND, id, Operation.ADD, friendId);
    }

    @Override
    public void deleteFriend(final long id, final long friendId) {
        getUser(id);
        getUser(friendId);
        if (Objects.equals(id, friendId)) {
            throw new ValidationException("Check that friend id is correct (you sent %s)".formatted(friendId));
        }
        if (userStorage.deleteFriend(id, friendId)) {
            // добавление события удаления друга в таблице events
            eventService.create(EventType.FRIEND, id, Operation.REMOVE, friendId);
        }
    }

    @Override
    public Collection<User> getFriends(final long id) {
        getUser(id);
        return userStorage.findFriends(id);
    }

    @Override
    public Collection<User> getCommonFriends(final long id, final long friendId) {
        getUser(id);
        getUser(friendId);
        return userStorage.findCommonFriends(id, friendId);
    }

    @Override
    public void deleteUserById(final long userId) {
        userStorage.delete(userId);
    }

    @Override
    public Collection<Event> getEvents(final long id) {
        getUser(id);
        return eventService.getEvents(id);
    }

    private void resetNameToLoginIfBlank(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
