package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film save(Film film);

    Optional<Film> findById(long id);

    Collection<Film> findAll();

    Collection<Film> findAllOrderByLikesDesc(long limit);

    Collection<Film> findByGenreIdOrderByLikesDesc(long genreId, long limit);

    Collection<Film> findByDirectorId(long directorId);

    Collection<Film> findByDirectorIdOrderByLikesDesc(long directorId);

    Collection<Film> findByDirectorIdOrderByYearAsc(long directorId);

    Collection<Film> findByUserId(long userId);

    Collection<Film> findByNameOrderByLikesDesc(String query);

    Collection<Film> findByDirectorNameOrderByLikesDesc(String query);

    Collection<Film> findByNameOrDirectorNameOrderByLikesDesc(String query);

    Collection<Film> findByReleaseYearOrderByLikesDesc(long releaseYear, long limit);

    Collection<Film> findByGenreIdAndReleaseYearOrderByLikesDesc(long genreId, long releaseYear, long limit);

    Optional<Film> update(Film film);

    void addLike(long id, long userId, int mark);

    boolean deleteLike(long id, long userId);

    boolean delete(long id);

    void deleteAll();
}
