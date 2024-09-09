package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film save(Film film);

    Optional<Film> findById(long id);

    Collection<Film> findAll();

    Collection<Film> findByNameOrderByLikesDesc(String query);

    Collection<Film> findByDirectorNameOrderByLikesDesc(String query);

    Collection<Film> findByNameOrDirectorNameOrderByLikesDesc(String query);

    Collection<Film> findByDirectorId(long directorId);

    Collection<Film> findByDirectorIdOrderByLikesDesc(long directorId);

    Collection<Film> findByDirectorIdOrderByYearAsc(long directorId);

    Collection<Film> findAllOrderByLikesDesc(long limit);

    Collection<Film> findByGenreIdOrderByLikesDesc(long genreId, long limit);

    Collection<Film> findByReleaseYearOrderByLikesDesc(long releaseYear, long limit);

    Collection<Film> findByGenreIdAndReleaseYearOrderByLikesDesc(long genreId, long releaseYear, long limit);

    Optional<Film> update(Film film);

    void addLike(long id, long userId);

    boolean deleteLike(long id, long userId);

    boolean delete(long id);

    void deleteAll();

    Collection<Film> findCommonByUserIdAndFriendId(long userId, long friendId);

    Collection<Film> findRecommendedByUserId(long userId);
}
