package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    Collection<Genre> findAll();

    Collection<Genre> findAllByFilmId(Long filmId);

    Map<Long, Collection<Genre>> findAllByFilmId(Set<Long> filmIds);

    Optional<Genre> findById(Long id);

    void saveFilmGenre(Long filmId, Genre genre);

    void deleteFilmGenre(Long filmId, Genre genre);
}
