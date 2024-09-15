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

    private final Map<Long, Set<Long>> likesByFilm;
    private final Map<Long, Set<Long>> likesByUser;

    public FilmInMemoryStorage() {
        super(Film::getId, Film::setId);
        this.likesByFilm = new HashMap<>();
        this.likesByUser = new HashMap<>();
    }

    @Override
    public Film save(final Film film) {
        return enrichFields(super.save(film));
    }

    @Override
    public Optional<Film> findById(final long id) {
        return super.findById(id).map(this::enrichFields);
    }

    @Override
    public Collection<Film> findAll() {
        return super.findAll().stream()
                .peek(this::enrichFields)
                .toList();
    }

    @Override
    public Collection<Film> findAllOrderByLikesDesc(final long limit) {
        return data.values().stream()
                .sorted(Comparator.comparingLong(this::countFilmLikes).thenComparing(byId))
                .limit(limit)
                .toList();
    }

    @Override
    public Collection<Film> findByGenreIdOrderByLikesDesc(long genreId, long limit) {
        return List.of();
    }

    @Override
    public Collection<Film> findByDirectorId(final long directorId) {
        return List.of();
    }

    @Override
    public Collection<Film> findByDirectorIdOrderByLikesDesc(final long directorId) {
        return List.of();
    }

    @Override
    public Collection<Film> findByDirectorIdOrderByYearAsc(final long directorId) {
        return List.of();
    }

    @Override
    public Collection<Film> findByUserId(long userId) {
        return List.of();
    }

    @Override
    public Collection<Film> findByNameOrderByLikesDesc(final String query) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Film> findByDirectorNameOrderByLikesDesc(final String query) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Film> findByNameOrDirectorNameOrderByLikesDesc(final String query) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Film> findByReleaseYearOrderByLikesDesc(long releaseYear, long limit) {
        return List.of();
    }

    @Override
    public Collection<Film> findByGenreIdAndReleaseYearOrderByLikesDesc(long genreId, long releaseYear, long limit) {
        return List.of();
    }

    @Override
    public Optional<Film> update(final Film film) {
        return super.update(film).map(this::enrichFields);
    }

    @Override
    public void addLike(final long id, final long userId, final int mark) {
        if (!data.containsKey(id)) {
            throw new RuntimeException("Cannot add like: film with id = %d does not exist".formatted(id));
        }
        likesByFilm.computeIfAbsent(id, key -> new HashSet<>()).add(userId);
        likesByUser.computeIfAbsent(userId, key -> new HashSet<>()).add(id);
    }

    @Override
    public boolean deleteLike(final long id, final long userId) {
        Set<Long> filmLikes = likesByFilm.get(id);
        if (filmLikes == null || !filmLikes.remove(userId)) {
            return false;
        }
        likesByUser.get(userId).remove(id);
        return true;
    }

    @Override
    public boolean delete(final long id) {
        Set<Long> filmLikes = likesByFilm.remove(id);
        if (filmLikes != null) {
            filmLikes.forEach(userId -> likesByUser.get(userId).remove(id));
        }
        return super.delete(id);
    }

    @Override
    public void deleteAll() {
        likesByFilm.clear();
        super.deleteAll();
    }

    private Film enrichFields(final Film film) {
        sortFilmGenres(film);
        sortFilmDirectors(film);
        updateFilmLikes(film);
        if (film.getGenres() == null) {
            film.setGenres(Collections.emptySet());
        }
        if (film.getDirectors() == null) {
            film.setDirectors(Collections.emptySet());
        }
        return film;
    }

    private void sortFilmGenres(final Film film) {
        final Collection<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            film.setGenres(genres.stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .toList()
            );
        }
    }

    private void sortFilmDirectors(final Film film) {
        final Collection<Director> directors = film.getDirectors();
        if (directors != null && !directors.isEmpty()) {
            film.setDirectors(directors.stream()
                    .sorted(Comparator.comparing(Director::getId))
                    .toList()
            );
        }
    }

    private void updateFilmLikes(final Film film) {
        film.setLikes(countFilmLikes(film));
    }

    private long countFilmLikes(final Film film) {
        return likesByFilm.getOrDefault(film.getId(), Collections.emptySet()).size();
    }
}
