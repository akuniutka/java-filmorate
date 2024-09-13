package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    User save(User user);

    Optional<User> findById(long id);

    Collection<User> findAll();

    Optional<User> update(User user);

    void addFriend(long id, long friendId);

    boolean deleteFriend(long id, long friendId);

    boolean delete(long id);

    void deleteAll();

    Set<User> findFriends(long id);
}
