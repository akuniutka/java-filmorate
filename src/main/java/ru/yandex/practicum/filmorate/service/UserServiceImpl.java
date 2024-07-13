package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private long lastUsedId;

    public UserServiceImpl(final UserStorage userStorage) {
        this.userStorage = userStorage;
        this.lastUsedId = 0L;
    }

    @Override
    public User create(final User user) {
        Objects.requireNonNull(user, "Cannot create user: is null");
        user.setId(++lastUsedId);
        resetNameToLoginIfBlank(user);
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
        userStorage.findById(userId).orElseThrow(() -> new NotFoundException("user", userId));
        resetNameToLoginIfBlank(newUser);
        userStorage.save(newUser);
        log.info("Updated user with id = {}: {}", userId, newUser);
        return newUser;
    }

    private void resetNameToLoginIfBlank(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
