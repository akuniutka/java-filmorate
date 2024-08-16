package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    Film save(Film film);

    Optional<Film> update(Film film);

    void delete(Long id);

    void deleteAll();
}
