package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private long lastUsedId;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
        this.lastUsedId = 0L;
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
        Objects.requireNonNull(film, "Cannot save film: is null");
        if (film.getId() == null) {
            film.setId(++lastUsedId);
        } else {
            lastUsedId = Long.max(lastUsedId, film.getId());
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
