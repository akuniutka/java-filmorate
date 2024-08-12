package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class UserInMemoryStorage implements UserStorage {

    private final Map<Long, User> users;
    private long lastUsedId;

    public UserInMemoryStorage() {
        this.users = new HashMap<>();
        this.lastUsedId = 0L;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(final Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(final User user) {
        Objects.requireNonNull(user, "Cannot save user: is null");
        user.setId(++lastUsedId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        Objects.requireNonNull(user, "Cannot update user: is null");
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void delete(final Long id) {
        users.remove(id);
    }

    @Override
    public void deleteAll() {
        users.clear();
    }
}
