package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {

    Collection<Film> getFilms();

    Collection<Film> getTopFilms(long limit, Long genreId, Integer year);

    Collection<Film> getFilmsByDirectorId(long directorId);

    Collection<Film> getFilmsByDirectorIdOrderByYear(long directorId);

    Collection<Film> getFilmsByDirectorIdOrderByLikes(long directorId);

    Film getFilm(long id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLike(long id, long userId);

    Collection<Film> getRecommendations(long userId);

    void deleteLike(long id, long userId);

    void deleteFilm(long id);

    Collection<Film> searchFilmsByTitle(String query);

    Collection<Film> searchFilmsByDirectorName(String query);

    Collection<Film> searchFilmsByTitleAndDirectorName(String query);

    Collection<Film> getCommonFilms(long id, long friendId);
}
