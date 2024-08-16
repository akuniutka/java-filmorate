package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.FriendService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.FriendStorage;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendServiceImpl implements FriendService {

    private final FriendStorage friendStorage;
    private final UserService userService;

    @Override
    public void createFriend(Long userId, Long friendId) {
        assertUserExist(userId);
        User friend = assertUserExist(friendId);
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Check that friend id is correct (you sent %s)".formatted(friendId));
        }
        friendStorage.save(userId, friend);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        assertUserExist(userId);
        User friend = assertUserExist(friendId);
        friendStorage.delete(userId, friend);
    }

    @Override
    public Collection<User> getFriendsByUserId(Long userId) {
        assertUserExist(userId);
        return friendStorage.findAllByUserId(userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        assertUserExist(userId);
        assertUserExist(friendId);
        return friendStorage.findAllCommonFriends(userId, friendId);
    }

    private User assertUserExist(Long id) {
        return userService.getUser(id).orElseThrow(
                () -> new NotFoundException(User.class, id)
        );
    }
}
