package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class FilmInMemoryStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private long lastUsedId;

    public FilmInMemoryStorage() {
        this.films = new HashMap<>();
        this.lastUsedId = 0L;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findById(final Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film save(final Film film) {
        Objects.requireNonNull(film, "Cannot save film: is null");
        film.setId(++lastUsedId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        Objects.requireNonNull(film, "Cannot update film: is null");
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void delete(final Long id) {
        films.remove(id);
    }

    @Override
    public void deleteAll() {
        films.clear();
    }
}
