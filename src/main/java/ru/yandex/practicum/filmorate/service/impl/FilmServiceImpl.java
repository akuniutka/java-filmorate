package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.service.api.GenreService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final GenreService genreService;
    private final UserService userService;

    @Override
    public Collection<Film> getFilms() {
        final Collection<Film> films = filmStorage.findAll();
        enrichFilmsWithGenres(films);
        return films;
    }

    @Override
    public Collection<Film> getTopFilmsByLikes(final long limit) {
        final Collection<Film> films = filmStorage.findAllOrderByLikesDesc(limit);
        enrichFilmsWithGenres(films);
        return films;
    }

    @Override
    public Optional<Film> getFilm(final long id) {
        Optional<Film> film = filmStorage.findById(id);
        film.ifPresent(f -> f.setGenres(genreService.getGenresByFilmId(f.getId())));
        return film;
    }

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
    public void addLike(final long id, final long userId) {
        assertFilmExist(id);
        assertUserExist(userId);
        filmStorage.addLike(id, userId);
    }

    @Override
    public void deleteLike(final long id, final long userId) {
        assertFilmExist(id);
        assertUserExist(userId);
        filmStorage.deleteLike(id, userId);
    }

    private void assertFilmExist(final long id) {
        filmStorage.findById(id).orElseThrow(() -> new NotFoundException(Film.class, id));
    }

    private void assertUserExist(final long userId) {
        userService.getUser(userId).orElseThrow(() -> new NotFoundException(User.class, userId));
    }

    private void enrichFilmsWithGenres(final Collection<Film> films) {
        final Set<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());
        final Map<Long, Collection<Genre>> genresByFilmId = genreService.getGenresByFilmId(filmIds);
        films.forEach(film -> film.setGenres(genresByFilmId.get(film.getId())));
    }
}
