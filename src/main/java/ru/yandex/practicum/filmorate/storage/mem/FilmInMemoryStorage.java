package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class FilmInMemoryStorage extends BaseInMemoryStorage<Film> implements FilmStorage {

    private final Map<Long, Set<Long>> likes;

    public FilmInMemoryStorage() {
        super(Film::getId, Film::setId);
        this.likes = new HashMap<>();
    }

    @Override
    public Collection<Film> findAllOrderByLikesDesc(final long limit, final Long genreId, final Integer year) {
        return films.values().stream()
                .sorted(Comparator.comparing(film -> likes.getOrDefault(film.getId(), Collections.emptySet()).size()))
                .limit(limit)
                .toList();
    }

    @Override
    public Collection<Film> findAllByDirectorId(long directorId) {
        return List.of();
    }

    @Override
    public Collection<Film> findAllByDirectorIdOrderByYear(long directorId) {
        return List.of();
    }

    @Override
    public Collection<Film> findAllByDirectorIdOrderByLikes(long directorId) {
        return List.of();
    }

    @Override
    public Film save(Film entity) {
        return sortFilmDirectors(sortFilmGenres(super.save(entity)));
    }

    @Override
    public Optional<Film> update(Film entity) {
        return super.update(entity).map(this::sortFilmGenres).map(this::sortFilmDirectors);
    }

    @Override
    public void addLike(long id, long userId) {
        if (!data.containsKey(id)) {
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
        super.delete(id);
    }

    @Override
    public void deleteAll() {
        likes.clear();
        super.deleteAll();
    }

    @Override
    public Collection<Film> searchFilmsByTitle(String query) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Film> searchFilmsByDirectorName(String query) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Film> searchFilmsByTitleAndDirectorName(String query) {
        return Collections.emptyList();
    }

    private Film sortFilmGenres(final Film film) {
        final Collection<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            film.setGenres(genres.stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .toList()
            );
        }
        return film;
    }

    private Film sortFilmDirectors(final Film film) {
        final Collection<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            film.setDirectors(directors.stream()
                    .sorted(Comparator.comparing(Director::getId))
                    .toList()
            );
        }
        return film;
    }

    private int countFilmLikes(final Film film) {
        return likes.getOrDefault(film.getId(), Collections.emptySet()).size();
    }

    @Override
    public Set<Long> getLikesByUserId(long userId) {
        return Set.of();
    }
}
