package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    Collection<User> getUsers();

    Collection<Event> getEvents(long id);

    Optional<User> getUser(long id);

    User createUser(User user);

    Optional<User> updateUser(User user);

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);

    Collection<User> getFriends(long id);

    Collection<User> getCommonFriends(long id, long friendId);
}
