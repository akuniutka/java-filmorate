package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    Collection<User> getUsers();

    Optional<User> getUser(Long id);

    User createUser(User user);

    Optional<User> updateUser(User user);
}
