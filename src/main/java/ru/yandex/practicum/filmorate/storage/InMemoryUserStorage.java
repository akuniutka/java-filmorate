package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(final Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public void save(final User user) {
        Objects.requireNonNull(user, "Cannot save user: is null");
        Objects.requireNonNull(user.getId(), "Cannot save user: user id is null");
        users.put(user.getId(), user);
    }

    @Override
    public void deleteById(final Long userId) {
        users.remove(userId);
    }

    @Override
    public void deleteAll() {
        users.clear();
    }
}
