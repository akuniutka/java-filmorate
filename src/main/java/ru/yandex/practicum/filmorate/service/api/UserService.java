package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserService {

    Collection<User> getUsers();

    User getUser(long id);

    User createUser(User user);

    User updateUser(User user);

    void addFriend(long id, long friendId);

    void deleteFriend(long id, long friendId);

    Collection<User> getFriends(long id);

    Collection<User> getCommonFriends(long id, long friendId);

    void deleteUserById(long userId);

    Collection<Event> getEvents(long id);

    Map<Film, Integer> getLikes(long id);
}
