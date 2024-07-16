package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    User create(User user);

    Collection<User> findAll();

    User update(User user);

    User findUserById(Long id);

    void addFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    Collection<User> findFriendsByUserId(Long id);

    Collection<User> findCommonFriendsByUserIds(Long id, Long otherId);
}
