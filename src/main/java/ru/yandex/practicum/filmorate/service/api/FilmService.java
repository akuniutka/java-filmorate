package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmService {

    Collection<Film> getFilms();

    Collection<Film> getTopFilmsByLikes(long limit);

    Collection<Film> getFilmsByDirectorId(long directorId);

    Collection<Film> getFilmsByDirectorIdOrderByYear(long directorId);

    Collection<Film> getFilmsByDirectorIdOrderByLikes(long directorId);

    Optional<Film> getFilm(long id);

    Film createFilm(Film film);

    Optional<Film> updateFilm(Film film);

    void addLike(long id, long userId);

    Collection<Film> getRecommendations(long userId);

    void deleteLike(long id, long userId);

    void deleteFilm(long id);

    Collection<Film> searchFilmsByTitle(String query);

    Collection<Film> searchFilmsByDirectorName(String query);

    Collection<Film> searchFilmsByTitleAndDirectorName(String query);

}
