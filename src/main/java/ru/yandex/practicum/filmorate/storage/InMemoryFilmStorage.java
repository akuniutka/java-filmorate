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
        Objects.requireNonNull(film, "Cannot save film: is null");
        Objects.requireNonNull(film.getId(), "Cannot save film: film id is null");
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
