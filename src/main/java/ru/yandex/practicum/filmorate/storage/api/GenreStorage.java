package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {

    Collection<Genre> findAll();

    Optional<Genre> findById(long id);

    Collection<Genre> findById(Collection<Long> ids);
}
