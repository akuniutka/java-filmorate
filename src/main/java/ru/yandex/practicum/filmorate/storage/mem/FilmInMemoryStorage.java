package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class FilmInMemoryStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private final Map<Long, Set<Long>> likes;
    private long lastUsedId;

    public FilmInMemoryStorage() {
        this.films = new HashMap<>();
        this.likes = new HashMap<>();
        this.lastUsedId = 0L;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Collection<Film> findAllOrderByLikesDesc(final long limit) {
        return films.values().stream()
                .sorted(Comparator.comparing(film -> likes.getOrDefault(film.getId(), Collections.emptySet()).size()))
                .limit(limit)
                .toList();
    }

    @Override
    public Optional<Film> findById(final long id) {
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
    public Optional<Film> update(final Film film) {
        Objects.requireNonNull(film, "Cannot update film: is null");
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void addLike(long id, long userId) {
        if (!films.containsKey(id)) {
            throw new RuntimeException("Cannot add like: film with id = %d does not exist".formatted(id));
        }
        likes.computeIfAbsent(id, key -> new HashSet<>()).add(userId);
    }

    @Override
    public void deleteLike(long id, long userId) {
        Optional.ofNullable(likes.get(id)).ifPresent(s -> s.remove(userId));
    }

    @Override
    public void delete(final long id) {
        likes.remove(id);
        films.remove(id);
    }

    @Override
    public void deleteAll() {
        likes.clear();
        films.clear();
    }
}
