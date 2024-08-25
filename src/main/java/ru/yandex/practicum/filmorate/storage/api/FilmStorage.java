package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Collection<Film> findAll();

    Collection<Film> findAllOrderByLikesDesc(long limit, Long genreId, Integer year);

    Collection<Film> findAllByDirectorId(long directorId);

    Collection<Film> findAllByDirectorIdOrderByYear(long directorId);

    Collection<Film> findAllByDirectorIdOrderByLikes(long directorId);

    Optional<Film> findById(long id);

    Film save(Film film);

    Optional<Film> update(Film film);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    void delete(long id);

    void deleteAll();

    Set<Long> getLikesByUserId(long userId);

    Collection<Film> searchFilmsByTitle(String query);

    Collection<Film> searchFilmsByDirectorName(String query);

    Collection<Film> searchFilmsByTitleAndDirectorName(String query);

    Collection<Film> getCommonFilms(long id, long friendId);
}
