package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmService {

    Collection<Film> getFilms();

    Collection<Film> getTopFilmsByLikes(long limit, Long genreId, Integer year);

    Optional<Film> getFilm(long id);

    Film createFilm(Film film);

    Optional<Film> updateFilm(Film film);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);
}
