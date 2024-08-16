package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User createUser(final User user) {
        Objects.requireNonNull(user, "Cannot create user: is null");
        resetNameToLoginIfBlank(user);
        final User userStored = userStorage.save(user);
        log.info("Created new user: {}", userStored);
        return userStored;
    }

    @Override
    public Collection<User> getUsers() {
        return userStorage.findAll();
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
    public Optional<User> getUser(final Long userId) {
        return userStorage.findById(userId);
    }

    private void resetNameToLoginIfBlank(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
