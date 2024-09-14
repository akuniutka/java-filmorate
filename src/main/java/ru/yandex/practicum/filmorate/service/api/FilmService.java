package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {

    Film createFilm(Film film);

    Film getFilm(long id);

    Collection<Film> getFilms();

    Collection<Film> getTopFilms(long count, Long genreId, Integer year);

    Collection<Film> getFilmsByDirectorId(long directorId, String sortBy);

    Film updateFilm(Film film);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    void deleteFilm(long id);

    Collection<Film> getFilmsByTitleAndDirectorName(String query, String by);

    Collection<Film> getLikedFilms(long userId);

    Collection<Film> getCommonFilms(long userId, long friendId);
}
