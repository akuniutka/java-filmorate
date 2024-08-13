package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreService {

    Collection<Genre> getGenres();

    Collection<Genre> getGenresByFilmId(Long filmId);

    Map<Long, Collection<Genre>> getGenresByFilmId(Set<Long> filmIds);

    void updateFilmGenres(Long filmId, Collection<Genre> genres);

    Optional<Genre> getGenre(Long id);
}
