package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    User create(User user);

    Collection<User> findAll();

    User update(User user);

    User findUserById(Long userId);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Collection<User> findFriendsByUserId(Long userId);

    Collection<User> findCommonFriendsByUserIds(Long userId1, Long friendId2);
}
