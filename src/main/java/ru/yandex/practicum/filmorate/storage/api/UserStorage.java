package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Optional<User> findById(long id);

    User save(User user);

    Optional<User> update(User user);

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);

    Collection<User> findFriends(long id);

    Collection<User> findCommonFriends(long id, long friendId);

    void delete(long id);

    void deleteAll();
}
