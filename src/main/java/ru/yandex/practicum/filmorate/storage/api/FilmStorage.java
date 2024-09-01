package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film save(Film film);

    Optional<Film> findById(long id);

    Collection<Film> findAll();

    Collection<Film> findAllByName(String query);

    Collection<Film> findAllByDirectorName(String query);

    Collection<Film> findAllByDirectorId(long directorId);

    Collection<Film> findAllByNameOrDirectorName(String query);

    Collection<Film> findAllByDirectorIdOrderByLikes(long directorId);

    Collection<Film> findAllByDirectorIdOrderByYear(long directorId);

    Collection<Film> findAllOrderByLikesDesc(long limit, Long genreId, Integer year);

    Optional<Film> update(Film film);

    void addLike(long id, long userId);

    boolean deleteLike(long id, long userId);

    boolean delete(long id);

    void deleteAll();

    Collection<Film> getCommonFilms(long id, long friendId);

    Set<Long> getLikesByUserId(long userId);
}
