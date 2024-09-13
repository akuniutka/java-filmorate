package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
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

    private static final String FIND_COMMON_FILMS_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN film_likes AS fl1 ON f.id = fl1.film_id
            JOIN film_likes AS fl2 ON f.id = fl2.film_id
            WHERE fl1.user_id = :userId AND fl2.user_id = :friendId
            ORDER BY f.likes DESC, f.id;
            """;
    private static final String FIND_RECOMMENDED_BY_USER_ID_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN film_likes AS fl1 ON f.id = fl1.film_id AND fl1.user_id =
            (
              SELECT fl1.user_id
              FROM film_likes AS fl1
              LEFT JOIN film_likes AS fl2 ON fl1.film_id = fl2.film_id
              WHERE fl1.user_id != :userId AND fl2.user_id = :userId
              GROUP BY fl1.user_id
              HAVING COUNT(fl2.user_id) > 0
              ORDER BY COUNT(fl2.user_id) DESC, COUNT(*) DESC, fl1.user_id
              LIMIT 1
            )
            LEFT JOIN film_likes AS fl2 ON f.id = fl2.film_id AND fl2.user_id = :userId
            WHERE fl2.film_id IS NULL
            ORDER BY f.likes DESC, f.id;
            """;

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
    public Collection<Film> findByNameOrderByLikesDesc(String query) {
        final String searchQuery = "%" + query + "%";
        return find(
                and().like("name", searchQuery),
                desc("likes").asc("id")
        );
    }

    @Override
    public Collection<Film> findByDirectorNameOrderByLikesDesc(String query) {
        final String searchQuery = "%" + query + "%";
        return find(
                and().like("directors", "name", searchQuery),
                desc("likes").asc("id")
        );
    }

    @Override
    public Collection<Film> findByNameOrDirectorNameOrderByLikesDesc(String query) {
        final String searchQuery = "%" + query + "%";
        return find(
                or().like("name", searchQuery).like("directors", "name", searchQuery),
                desc("likes").asc("id")
        );
    }

    @Override
    public Collection<Film> findByDirectorId(final long directorId) {
        return find(
                and().eq("directors", "id", directorId)
        );
    }

    @Override
    public Collection<Film> findByDirectorIdOrderByLikesDesc(final long directorId) {
        return find(
                and().eq("directors", "id", directorId),
                desc("likes").asc("id")
        );
    }

    @Override
    public Collection<Film> findByDirectorIdOrderByYearAsc(final long directorId) {
        return find(
                and().eq("directors", "id", directorId),
                asc("releaseYear").asc("id")
        );
    }

    @Override
    public Collection<Film> findAllOrderByLikesDesc(final long limit) {
        return findAll(
                desc("likes").asc("id"),
                limit
        );
    }

    @Override
    public Collection<Film> findByGenreIdOrderByLikesDesc(final long genreId, final long limit) {
        return find(
                and().eq("genres", "id", genreId),
                desc("likes").asc("id"),
                limit
        );
    }

    @Override
    public Collection<Film> findByReleaseYearOrderByLikesDesc(long releaseYear, long limit) {
        return find(
                and().eq("releaseYear", releaseYear),
                desc("likes").asc("id"),
                limit
        );
    }

    @Override
    public Collection<Film> findByGenreIdAndReleaseYearOrderByLikesDesc(long genreId, long releaseYear, long limit) {
        return find(
                and().eq("genres", "id", genreId).eq("releaseYear", releaseYear),
                desc("likes").asc("id"),
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
    public Collection<Film> findCommonByUserIdAndFriendId(long userId, long friendId) {
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId);
        return findMany(FIND_COMMON_FILMS_QUERY, params);
    }

    @Override
    public Collection<Film> findRecommendedByUserId(final long userId) {
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return findMany(FIND_RECOMMENDED_BY_USER_ID_QUERY, params);
    }

    @Override
    protected List<Film> findMany(String query, SqlParameterSource params) {
        return fetchRelations(super.findMany(query, params));
    }

    private Film fetchRelations(final Film film) {
        return Optional.of(film)
                .map(this::fetchMpa)
                .map(this::fetchGenres)
                .map(this::fetchDirectors)
                .orElseThrow();
    }

    private List<Film> fetchRelations(final List<Film> films) {
        return Optional.of(films)
                .map(this::fetchMpa)
                .map(this::fetchGenres)
                .map(this::fetchDirectors)
                .orElseThrow();
    }

    private Film fetchMpa(final Film film) {
        film.setMpa(mpa.fetchRelation(film.getId()).orElse(null));
        return film;
    }

    private List<Film> fetchMpa(final List<Film> films) {
        final Set<Long> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());
        final Map<Long, Mpa> mpas = mpa.fetchRelations(ids);
        return films.stream()
                .peek(film -> film.setMpa(mpas.get(film.getId())))
                .toList();
    }

    private Film fetchGenres(final Film film) {
        film.setGenres(genres.fetchRelations(film.getId()));
        return film;
    }

    private List<Film> fetchGenres(final List<Film> films) {
        final Map<Long, Set<Genre>> genresByFilmId = genres.fetchRelations(getFilmIds(films));
        return films.stream()
                .peek(film -> film.setGenres(genresByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>())))
                .toList();
    }

    private Film fetchDirectors(final Film film) {
        film.setDirectors(directors.fetchRelations(film.getId()));
        return film;
    }

    private List<Film> fetchDirectors(final List<Film> films) {
        Map<Long, Set<Director>> directorsByFilmId = directors.fetchRelations(getFilmIds(films));
        return films.stream()
                .peek(film -> film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), new LinkedHashSet<>())))
                .toList();
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
