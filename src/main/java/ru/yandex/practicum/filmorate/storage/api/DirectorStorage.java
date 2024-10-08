package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {

    Collection<Director> findAll();

    Optional<Director> findById(long id);

    Collection<Director> findById(Collection<Long> ids);

    Director save(Director director);

    Optional<Director> update(Director director);

    boolean delete(long id);

    void deleteAll();
}
