package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendStorage {

    void save(Long userId, User friend);

    void delete(Long userId, User friend);

    Collection<User> findAllByUserId(Long userId);

    Collection<User> findAllCommonFriends(Long userId, Long friendId);
}
