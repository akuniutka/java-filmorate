package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {

    Collection<Genre> findAll();

    Collection<Genre> findAllByFilmId(Long filmId);

    Optional<Genre> findById(Long id);

    void saveFilmGenre(Long filmId, Genre genre);

    void deleteFilmGenre(Long filmId, Genre genre);
}
