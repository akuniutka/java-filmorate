package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.service.api.GenreService;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final GenreService genreService;

    @Override
    public Film createFilm(final Film film) {
        Objects.requireNonNull(film, "Cannot create film: is null");
        final Film filmStored = filmStorage.save(film);
        if (film.getGenres() != null) {
            genreService.updateFilmGenres(filmStored.getId(), film.getGenres());
        }
        filmStored.setGenres(genreService.getGenresByFilmId(filmStored.getId()));
        log.info("Created new film: {}", filmStored);
        return filmStored;
    }

    @Override
    public Collection<Film> getFilms() {
        return filmStorage.findAll().stream()
                .peek(f -> f.setGenres(genreService.getGenresByFilmId(f.getId())))
                .toList();
    }

    @Override
    public Optional<Film> updateFilm(final Film film) {
        Objects.requireNonNull(film, "Cannot update film: is null");
        final Optional<Film> filmStored = filmStorage.update(film);
        if (filmStored.isPresent()) {
            genreService.updateFilmGenres(film.getId(),
                    Optional.ofNullable(film.getGenres()).orElse(Collections.emptySet()));
            filmStored.get().setGenres(genreService.getGenresByFilmId(filmStored.get().getId()));
            log.info("Updated film: {}", filmStored.get());
        }
        return filmStored;
    }

    @Override
    public Optional<Film> getFilm(final Long id) {
        Optional<Film> film = filmStorage.findById(id);
        film.ifPresent(f -> f.setGenres(genreService.getGenresByFilmId(f.getId())));
        return film;
    }
}
