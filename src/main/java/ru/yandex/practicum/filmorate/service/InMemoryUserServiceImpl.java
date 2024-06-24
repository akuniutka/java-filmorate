package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class InMemoryUserServiceImpl implements UserService {

    private final Map<Long, User> users;
    private long lastUsedId;

    public InMemoryUserServiceImpl() {
        this.users = new HashMap<>();
        this.lastUsedId = 0L;
    }

    @Override
    public User create(final User user) {
        user.setId(++lastUsedId);
        save(user);
        log.info("Created new user: {}", user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User update(final User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Cannot update user with id={}: user not found", user.getId());
            throw new NotFoundException("User not found");
        }
        save(user);
        log.info("Updated user with id={}: {}", user.getId(), user);
        return user;
    }

    private void save(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
    }
}
