package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {

    Collection<Mpa> findAll();

    Optional<Mpa> findById(long id);
}
