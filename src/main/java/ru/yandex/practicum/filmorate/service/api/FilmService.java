package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmService {

    Film createFilm(Film film);

    Collection<Film> getFilms();

    Optional<Film> updateFilm(Film film);

    Optional<Film> getFilm(Long id);
}
