package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NullIdException;
import ru.yandex.practicum.filmorate.exception.NullModelException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findById(final Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public void save(final Film film) {
        if (film == null) {
            log.warn("Cannot save film: is null");
            throw new NullModelException("Film is null");
        } else if (film.getId() == null) {
            log.warn("Cannot save film: film id is null");
            throw new NullIdException("Film id is null");
        }
        films.put(film.getId(), film);
    }

    @Override
    public void deleteById(final Long filmId) {
        films.remove(filmId);
    }

    @Override
    public void deleteAll() {
        films.clear();
    }
}
