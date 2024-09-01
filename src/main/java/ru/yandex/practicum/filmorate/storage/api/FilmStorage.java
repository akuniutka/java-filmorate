package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film save(Film film);

    Optional<Film> findById(long id);

    Collection<Film> findAll();

    Collection<Film> findAllByNameOrderByLikesDesc(String query);

    Collection<Film> findAllByDirectorNameOrderByLikesDesc(String query);

    Collection<Film> findAllByNameOrDirectorNameOrderByLikesDesc(String query);

    Collection<Film> findAllByDirectorId(long directorId);

    Collection<Film> findAllByDirectorIdOrderByLikesDesc(long directorId);

    Collection<Film> findAllByDirectorIdOrderByYearAsc(long directorId);

    Collection<Film> findAllOrderByLikesDesc(long limit);

    Collection<Film> findAllByGenreIdOrderByLikesDesc(long genreId, long limit);

    Collection<Film> findAllByReleaseYearOrderByLikesDesc(long releaseYear, long limit);

    Collection<Film> findAllByGenreIdAndReleaseYearOrderByLikesDesc(long genreId, long releaseYear, long limit);

    Optional<Film> update(Film film);

    void addLike(long id, long userId);

    boolean deleteLike(long id, long userId);

    boolean delete(long id);

    void deleteAll();

    Collection<Film> getCommonFilms(long id, long friendId);

    Set<Long> getLikesByUserId(long userId);
}
