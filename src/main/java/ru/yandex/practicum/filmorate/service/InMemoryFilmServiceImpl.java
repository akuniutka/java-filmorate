package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class InMemoryFilmServiceImpl implements FilmService {

    private final Map<Long, Film> films;
    private long lastUsedId;

    public InMemoryFilmServiceImpl() {
        this.films = new HashMap<>();
        this.lastUsedId = 0L;
    }

    @Override
    public Film create(final Film film) {
        film.setId(++lastUsedId);
        films.put(film.getId(), film);
        log.info("Created new film: {}", film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film update(final Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Cannot update film with id={}: film not found", film.getId());
            throw new NotFoundException("Film not found");
        }
        films.put(film.getId(), film);
        log.info("Updated film with id={}: {}", film.getId(), film);
        return film;
    }
}
