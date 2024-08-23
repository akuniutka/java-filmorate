package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class UserInMemoryStorage implements UserStorage {

    private final Map<Long, User> users;
    private final Map<Long, Set<Long>> friends;
    private long lastUsedId;

    public UserInMemoryStorage() {
        this.users = new HashMap<>();
        this.friends = new HashMap<>();
        this.lastUsedId = 0L;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(final long id) {
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
    public Optional<User> update(final User user) {
        Objects.requireNonNull(user, "Cannot update user: is null");
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void addFriend(final long id, final long friendId) {
        if (!users.containsKey(id)) {
            throw new RuntimeException("Cannot add friend: user with id = %d does not exist".formatted(id));
        }
        if (!users.containsKey(friendId)) {
            throw new RuntimeException("Cannot add friend: friend with id = %d does not exist".formatted(friendId));
        }
        if (id == friendId) {
            throw new RuntimeException("Cannot add friend: it is user themself");
        }
        friends.computeIfAbsent(id, key -> new HashSet<>()).add(friendId);
    }

    @Override
    public void deleteFriend(final long id, final long friendId) {
        Optional.ofNullable(friends.get(id)).ifPresent(s -> s.remove(friendId));
    }

    @Override
    public Collection<User> findFriends(final long id) {
        if (!users.containsKey(id)) {
            throw new RuntimeException("Cannot get friends: user with id = %d does not exist".formatted(id));
        }
        return friends.getOrDefault(id, new HashSet<>()).stream()
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<User> findCommonFriends(final long id, final long friendId) {
        if (!users.containsKey(id)) {
            throw new RuntimeException("Cannot get common friends: user with id = %d does not exist".formatted(id));
        }
        if (!users.containsKey(friendId)) {
            throw new RuntimeException("Cannot get common friends: friend with id = %d does not exist"
                    .formatted(friendId));
        }
        Set<Long> friendFriends = friends.getOrDefault(friendId, new HashSet<>());
        return findFriends(id).stream()
                .filter(user -> friendFriends.contains(user.getId()))
                .toList();
    }

    @Override
    public void deleteAll() {
        friends.clear();
        users.clear();
    }

    @Override
    public void deleteById(long userId) {}
}
