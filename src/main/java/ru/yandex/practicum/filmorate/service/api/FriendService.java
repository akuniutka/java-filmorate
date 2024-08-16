package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendService {

    void createFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> getFriendsByUserId(Long userId);

    Collection<User> getCommonFriends(Long userId, Long friendId);
}
