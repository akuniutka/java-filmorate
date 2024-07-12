package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullModelException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

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
        if (user == null) {
            log.warn("Cannot create user: is null");
            throw new NullModelException("User is null");
        }
        user.setId(++lastUsedId);
        save(user);
        log.info("Created new user: {}", user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User update(final User newUser) {
        if (newUser == null) {
            log.warn("Cannot update user: is null");
            throw new NullModelException("User is null");
        }
        Optional<User> oldUserOpt = userStorage.findById(newUser.getId());
        if (oldUserOpt.isEmpty()) {
            log.warn("Cannot update user with id={}: user not found", newUser.getId());
            throw new NotFoundException("User not found");
        }
        save(newUser);
        log.info("Updated user with id = {}: {}", newUser.getId(), newUser);
        return newUser;
    }

    private void save(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.save(user);
    }
}
