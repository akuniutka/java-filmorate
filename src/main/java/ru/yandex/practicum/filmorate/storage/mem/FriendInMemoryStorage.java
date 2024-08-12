package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.FriendStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class FriendInMemoryStorage implements FriendStorage {

    private final Map<Long, Set<User>> friends;

    FriendInMemoryStorage() {
        this.friends = new HashMap<>();
    }

    @Override
    public void save(Long userId, User friend) {
        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friend);
    }

    @Override
    public void delete(Long userId, User friend) {
        Optional.ofNullable(friends.get(userId)).ifPresent(s -> s.remove(friend));
    }

    @Override
    public Collection<User> findAllByUserId(Long userId) {
        return friends.getOrDefault(userId, Collections.emptySet()).stream()
                .sorted(Comparator.comparing(User::getId))
                .toList();
    }

    @Override
    public Collection<User> findAllCommonFriends(Long userId, Long friendId) {
        final Set<User> userFriends = friends.getOrDefault(userId, Collections.emptySet());
        final Set<User> friendFriends = friends.getOrDefault(friendId, Collections.emptySet());
        userFriends.retainAll(friendFriends);
        return userFriends.stream()
                .sorted(Comparator.comparing(User::getId))
                .toList();
    }
}
