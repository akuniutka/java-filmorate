package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NullModelException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private long lastUsedId;

    public FilmServiceImpl(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        this.lastUsedId = 0L;
    }

    @Override
    public Film create(final Film film) {
        if (film == null) {
            log.warn("Cannot create film: is null");
            throw new NullModelException("Film is null");
        }
        film.setId(++lastUsedId);
        filmStorage.save(film);
        log.info("Created new film: {}", film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film update(final Film newFilm) {
        if (newFilm == null) {
            log.warn("Cannot update film: is null");
            throw new NullModelException("Film is null");
        }
        final Long filmId = newFilm.getId();
        filmStorage.findById(filmId).orElseThrow(() -> new NotFoundException("film", filmId));
        filmStorage.save(newFilm);
        log.info("Updated film with id={}: {}", newFilm.getId(), newFilm);
        return newFilm;
    }
}
