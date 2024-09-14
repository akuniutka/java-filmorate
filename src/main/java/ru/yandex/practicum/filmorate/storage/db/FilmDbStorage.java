package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private final ManyToOneRelation<Mpa> mpa;
    private final ManyToManyRelation<Genre> genres;
    private final ManyToManyRelation<Director> directors;
    private final ManyToManyRelation<User> likes;

    @Autowired
    public FilmDbStorage(final NamedParameterJdbcTemplate jdbc) {
        super(Film.class, jdbc);
        this.mpa = new ManyToOneRelation<Mpa>(Mpa.class);
        this.genres = new ManyToManyRelation<Genre>(Genre.class);
        this.directors = new ManyToManyRelation<Director>(Director.class);
        this.likes = new ManyToManyRelation<>(User.class)
                .withJoinTable("film_likes");
    }

    @Override
    public Film save(final Film film) {
        final Film savedFilm = save(List.of("name", "description", "releaseDate", "duration"), film);
        Optional.ofNullable(film.getMpa()).ifPresent(m -> mpa.addRelation(savedFilm.getId(), m.getId()));
        genres.saveRelations(savedFilm.getId(), getGenresIds(film.getGenres()));
        directors.saveRelations(savedFilm.getId(), getDirectorIds(film.getDirectors()));
        return fetchRelations(savedFilm);
    }

    @Override
    public Optional<Film> findById(final long id) {
        return super.findById(id).map(this::fetchRelations);
    }

    @Override
    public Collection<Film> findAllOrderByLikesDesc(final long limit) {
        return findAll(
                orderBy("likes", Order.DESC).andThenBy("id"),
                limit
        );
    }

    @Override
    public Collection<Film> findByGenreIdOrderByLikesDesc(final long genreId, final long limit) {
        return find(
                where(genres, "id", Operand.EQ, genreId),
                orderBy("likes", Order.DESC).andThenBy("id"),
                limit
        );
    }

    @Override
    public Collection<Film> findByDirectorId(final long directorId) {
        return find(
                where(directors, "id", Operand.EQ, directorId)
        );
    }

    @Override
    public Collection<Film> findByDirectorIdOrderByLikesDesc(final long directorId) {
        return find(
                where(directors, "id", Operand.EQ, directorId),
                orderBy("likes", Order.DESC).andThenBy("id")
        );
    }

    @Override
    public Collection<Film> findByDirectorIdOrderByYearAsc(final long directorId) {
        return find(
                where(directors, "id", Operand.EQ, directorId),
                orderBy("releaseYear").andThenBy("id")
        );
    }

    @Override
    public Collection<Film> findByUserId(final long userId) {
        return find(
                where(likes, "id", Operand.EQ, userId)
        );
    }

    @Override
    public Collection<Film> findByNameOrderByLikesDesc(String query) {
        final String searchQuery = "%" + query + "%";
        return find(
                where("name", Operand.LIKE, searchQuery),
                orderBy("likes", Order.DESC).andThenBy("id")
        );
    }

    @Override
    public Collection<Film> findByDirectorNameOrderByLikesDesc(String query) {
        final String searchQuery = "%" + query + "%";
        return find(
                where(directors, "name", Operand.LIKE, searchQuery),
                orderBy("likes", Order.DESC).andThenBy("id")
        );
    }

    @Override
    public Collection<Film> findByNameOrDirectorNameOrderByLikesDesc(String query) {
        final String searchQuery = "%" + query + "%";
        return find(
                where("name", Operand.LIKE, searchQuery).or(directors, "name", Operand.LIKE, searchQuery),
                orderBy("likes", Order.DESC).andThenBy("id")
        );
    }

    @Override
    public Collection<Film> findByReleaseYearOrderByLikesDesc(long releaseYear, long limit) {
        return find(
                where("releaseYear", Operand.EQ, releaseYear),
                orderBy("likes", Order.DESC).andThenBy("id"),
                limit
        );
    }

    @Override
    public Collection<Film> findByGenreIdAndReleaseYearOrderByLikesDesc(long genreId, long releaseYear, long limit) {
        return find(
                where(genres, "id", Operand.EQ, genreId).and("releaseYear", Operand.EQ, releaseYear),
                orderBy("likes", Order.DESC).andThenBy("id"),
                limit
        );
    }

    @Override
    public Optional<Film> update(final Film film) {
        final Optional<Film> savedFilm = update(List.of("name", "description", "releaseDate", "duration"), film);
        savedFilm.ifPresent(f -> {
            Optional.ofNullable(film.getMpa()).ifPresentOrElse(
                    m -> mpa.addRelation(film.getId(), m.getId()),
                    () -> mpa.dropRelation(film.getId())
            );
            genres.saveRelations(film.getId(), getGenresIds(film.getGenres()));
            directors.saveRelations(film.getId(), getDirectorIds(film.getDirectors()));
        });
        return savedFilm.map(this::fetchRelations);
    }

    @Override
    public void addLike(final long id, final long userId) {
        likes.addRelation(id, userId);
    }

    @Override
    public boolean deleteLike(final long id, final long userId) {
        return likes.dropRelation(id, userId);
    }

    @Override
    protected List<Film> findMany(String query, SqlParameterSource params) {
        final List<Film> films = super.findMany(query, params);
        if (!CollectionUtils.isEmpty(films)) {
            fetchMpa(films);
            fetchGenres(films);
            fetchDirectors(films);
        }
        return films;
    }

    private Film fetchRelations(final Film film) {
        return Optional.of(film)
                .map(this::fetchMpa)
                .map(this::fetchGenres)
                .map(this::fetchDirectors)
                .orElseThrow();
    }

    private Film fetchMpa(final Film film) {
        film.setMpa(mpa.fetchRelation(film.getId()).orElse(null));
        return film;
    }

    private void fetchMpa(final List<Film> films) {
        final Set<Long> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());
        final Map<Long, Mpa> mpaByFilmId = mpa.fetchRelations(ids);
        films.forEach(film -> film.setMpa(mpaByFilmId.get(film.getId())));
    }

    private Film fetchGenres(final Film film) {
        film.setGenres(genres.fetchRelations(film.getId()));
        return film;
    }

    private void fetchGenres(final List<Film> films) {
        final Map<Long, Set<Genre>> genresByFilmId = genres.fetchRelations(getFilmIds(films));
        films.forEach(film -> film.setGenres(genresByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>())));
    }

    private Film fetchDirectors(final Film film) {
        film.setDirectors(directors.fetchRelations(film.getId()));
        return film;
    }

    private void fetchDirectors(final List<Film> films) {
        final Map<Long, Set<Director>> directorsByFilmId = directors.fetchRelations(getFilmIds(films));
        films.forEach(film -> film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>())));
    }

    private Set<Long> getFilmIds(final Collection<Film> films) {
        return films == null ? null : films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> getGenresIds(final Collection<Genre> genres) {
        return genres == null ? null : genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> getDirectorIds(final Collection<Director> directors) {
        return directors == null ? null : directors.stream()
                .map(Director::getId)
                .collect(Collectors.toSet());
    }
}
