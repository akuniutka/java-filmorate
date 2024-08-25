package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class UserInMemoryStorage extends BaseInMemoryStorage<User> implements UserStorage {

    private final Map<Long, Set<Long>> friends;

    public UserInMemoryStorage() {
        super(User::getId, User::setId);
        this.friends = new HashMap<>();
    }

    @Override
    public void addFriend(final long id, final long friendId) {
        if (!data.containsKey(id)) {
            throw new RuntimeException("Cannot add friend: user with id = %d does not exist".formatted(id));
        }
        if (!data.containsKey(friendId)) {
            throw new RuntimeException("Cannot add friend: friend with id = %d does not exist".formatted(friendId));
        }
        if (id == friendId) {
            throw new RuntimeException("Cannot add friend: it is user themself");
        }
        friends.computeIfAbsent(id, key -> new HashSet<>()).add(friendId);
    }

    @Override
    public boolean deleteFriend(final long id, final long friendId) {
        Set<Long> userFriends = friends.get(id);
        if (userFriends == null) {
            return false;
        }
        return userFriends.remove(friendId);
    }

    @Override
    public Collection<User> findFriends(final long id) {
        if (!data.containsKey(id)) {
            throw new RuntimeException("Cannot get friends: user with id = %d does not exist".formatted(id));
        }
        return friends.getOrDefault(id, new HashSet<>()).stream()
                .map(data::get)
                .sorted(byId)
                .toList();
    }

    @Override
    public Collection<User> findCommonFriends(final long id, final long friendId) {
        if (!data.containsKey(id)) {
            throw new RuntimeException("Cannot get common friends: user with id = %d does not exist".formatted(id));
        }
        if (!data.containsKey(friendId)) {
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
        super.deleteAll();
    }

    @Override
    public void deleteById(long userId) {

    }
}
