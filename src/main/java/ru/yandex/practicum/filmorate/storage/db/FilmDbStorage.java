package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private static final String ADD_LIKE_QUERY = """
            MERGE INTO film_likes
            KEY (film_id, user_id)
            VALUES (:id, :userId);
            """;
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM film_likes
            WHERE film_id = :id AND user_id = :userId;
            """;
    private static final String SAVE_FILM_MPA_QUERY = """
            UPDATE films
            SET mpa_id = :mpaId
            WHERE id = :id;
            """;
    private static final String SAVE_FILM_GENRE_QUERY = """
            MERGE INTO film_genres
            KEY (film_id, genre_id)
            VALUES (:id, :genreId);
            """;
    private static final String SAVE_FILM_DIRECTOR_QUERY = """
            MERGE INTO film_directors
            KEY (film_id, director_id)
            VALUES (:id, :directorId);
            """;
    private static final String FIND_FILM_MPA_QUERY = """
            SELECT m.*
            FROM mpas AS m
            JOIN films AS f ON m.id = f.mpa_id
            WHERE f.id = :id;
            """;
    private static final String FIND_FILMS_MPA_QUERY = """
            SELECT f.id AS film_id,
              m.*
            FROM mpas AS m
            JOIN films AS f ON m.id = f.mpa_id
            WHERE f.id IN (%s);
            """;
    private static final String FIND_FILM_GENRES_QUERY = """
            SELECT g.*
            FROM genres AS g
            JOIN film_genres AS fg ON g.id = fg.genre_id
            WHERE fg.film_id = :id
            ORDER BY g.id;
            """;
    private static final String FIND_FILMS_GENRES_QUERY = """
            SELECT fg.film_id,
              g.*
            FROM genres AS g
            JOIN film_genres AS fg ON g.id = fg.genre_id
            WHERE fg.film_id IN (%s)
            ORDER BY g.id;
            """;
    private static final String FIND_FILM_DIRECTORS_QUERY = """
            SELECT d.*
            FROM directors AS d
            JOIN film_directors AS fd ON d.id = fd.director_id
            WHERE fd.film_id = :id
            ORDER BY d.id
            """;
    private static final String FIND_FILMS_DIRECTORS_QUERY = """
            SELECT fd.film_id,
              d.*
            FROM directors AS d
            JOIN film_directors AS fd ON d.id = fd.director_id
            WHERE fd.film_id IN (%s)
            ORDER BY d.id;
            """;
    private static final String DELETE_FILM_GENRES_QUERY = """
            DELETE FROM film_genres
            WHERE film_id = :id AND genre_id NOT IN (%s);
            """;
    private static final String DELETE_ALL_FILM_GENRES_QUERY = """
            DELETE FROM film_genres
            WHERE film_id = :id;
            """;
    private static final String DELETE_FILM_DIRECTORS_QUERY = """
            DELETE FROM film_directors
            WHERE film_id = :id AND director_id NOT IN (%s);
            """;
    private static final String DELETE_ALL_FILM_DIRECTORS_QUERY = """
            DELETE FROM film_directors
            WHERE film_id = :id;
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

    private final RowMapper<Mpa> mpaMapper;
    private final RowMapper<Genre> genreMapper;
    private final RowMapper<Director> directorMapper;

    @Autowired
    public FilmDbStorage(final NamedParameterJdbcTemplate jdbc) {
        super(Film.class, jdbc);
        this.mpaMapper = new BeanPropertyRowMapper<>(Mpa.class);
        this.genreMapper = new BeanPropertyRowMapper<>(Genre.class);
        this.directorMapper = new BeanPropertyRowMapper<>(Director.class);
    }

    @Override
    public Film save(final Film film) {
        final Film savedFilm = save(List.of("name", "description", "releaseDate", "duration"), film);
        saveFilmMpa(savedFilm.getId(), film.getMpa());
        saveFilmGenres(savedFilm.getId(), film.getGenres());
        saveFilmDirectors(savedFilm.getId(), film.getDirectors());
        return fetchCollections(savedFilm);
    }

    @Override
    public Optional<Film> findById(final long id) {
        return super.findById(id).map(this::fetchCollections);
    }

    @Override
    public Collection<Film> findAll() {
        return super.findAll();
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
            saveFilmMpa(film.getId(), film.getMpa());
            updateFilmGenres(film.getId(), film.getGenres());
            updateFilmDirectors(film.getId(), film.getDirectors());
        });
        return savedFilm.map(this::fetchCollections);
    }

    @Override
    public void addLike(final long id, final long userId) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId);
        execute(ADD_LIKE_QUERY, params);
    }

    @Override
    public boolean deleteLike(final long id, final long userId) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId);
        return execute(DELETE_LIKE_QUERY, params) > 0;
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
        return fetchCollections(super.findMany(query, params));
    }

    private Film fetchCollections(final Film film) {
        return Optional.of(film)
                .map(this::fetchMpa)
                .map(this::fetchGenres)
                .map(this::fetchDirectors)
                .orElseThrow();
    }

    private List<Film> fetchCollections(final List<Film> films) {
        return Optional.of(films)
                .map(this::fetchMpa)
                .map(this::fetchGenres)
                .map(this::fetchDirectors)
                .orElseThrow();
    }

    private Film fetchMpa(final Film film) {
        final SqlParameterSource params = new MapSqlParameterSource("id", film.getId());
        try {
            Mpa mpa = jdbc.queryForObject(FIND_FILM_MPA_QUERY, params, mpaMapper);
            film.setMpa(mpa);
        } catch (EmptyResultDataAccessException ignored) {
            film.setMpa(null);
        }
        return film;
    }

    private List<Film> fetchMpa(final List<Film> films) {
        final String filmIds = films.stream()
                .map(Film::getId)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        final Map<Long, Mpa> mpas = new HashMap<>();
        jdbc.getJdbcOperations().query(FIND_FILMS_MPA_QUERY.formatted(filmIds),
                rs -> {
                    Long filmId = rs.getLong("film_id");
                    Mpa mpa = mpaMapper.mapRow(rs, 0);
                    mpas.put(filmId, mpa);
                }
        );
        return films.stream()
                .peek(film -> film.setMpa(mpas.get(film.getId())))
                .toList();
    }

    private Film fetchGenres(final Film film) {
        var params = new MapSqlParameterSource("id", film.getId());
        final Collection<Genre> genres = jdbc.query(FIND_FILM_GENRES_QUERY, params, genreMapper);
        film.setGenres(genres);
        return film;
    }

    private List<Film> fetchGenres(final List<Film> films) {
        final String filmIds = films.stream()
                .map(Film::getId)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        final Map<Long, Collection<Genre>> genresByFilmId = new HashMap<>();
        jdbc.getJdbcOperations().query(FIND_FILMS_GENRES_QUERY.formatted(filmIds),
                rs -> {
                    Long filmId = rs.getLong("film_id");
                    Genre genre = genreMapper.mapRow(rs, 0);
                    genresByFilmId.computeIfAbsent(filmId, key -> new ArrayList<>()).add(genre);
                }
        );
        return films.stream()
                .peek(film -> film.setGenres(genresByFilmId.getOrDefault(film.getId(), new HashSet<>())))
                .toList();
    }

    private Film fetchDirectors(final Film film) {
        var params = new MapSqlParameterSource("id", film.getId());
        Collection<Director> directors = jdbc.query(FIND_FILM_DIRECTORS_QUERY, params, directorMapper);
        film.setDirectors(directors);
        return film;
    }

    private List<Film> fetchDirectors(final List<Film> films) {
        final String filmIds = films.stream()
                .map(Film::getId)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        final Map<Long, Collection<Director>> directorsByFilmId = new HashMap<>();
        jdbc.getJdbcOperations().query(FIND_FILMS_DIRECTORS_QUERY.formatted(filmIds),
                rs -> {
                    Long filmId = rs.getLong("film_id");
                    Director director = directorMapper.mapRow(rs, 0);
                    directorsByFilmId.computeIfAbsent(filmId, key -> new ArrayList<>()).add(director);
                }
        );
        return films.stream()
                .peek(film -> film.setDirectors(directorsByFilmId.getOrDefault(film.getId(), new ArrayList<>())))
                .toList();
    }

    private void saveFilmMpa(final long id, final Mpa mpa) {
        final Long mpaId = mpa == null ? null : mpa.getId();
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("mpaId", mpaId);
        execute(SAVE_FILM_MPA_QUERY, params);
    }

    private void saveFilmGenres(final long id, final Collection<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            final SqlParameterSource[] params = genres.stream()
                    .map(genre -> new MapSqlParameterSource()
                            .addValue("id", id)
                            .addValue("genreId", genre.getId())
                    )
                    .toArray(SqlParameterSource[]::new);
            jdbc.batchUpdate(SAVE_FILM_GENRE_QUERY, params);
        }
    }

    private void updateFilmGenres(final long id, final Collection<Genre> genres) {
        saveFilmGenres(id, genres);
        deleteFilmGenresExcept(id, genres);
    }

    private void deleteFilmGenresExcept(final long id, final Collection<Genre> genres) {
        var params = new MapSqlParameterSource("id", id);
        if (genres == null || genres.isEmpty()) {
            execute(DELETE_ALL_FILM_GENRES_QUERY, params);
        } else {
            final String genreIds = genres.stream()
                    .map(Genre::getId)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            execute(DELETE_FILM_GENRES_QUERY.formatted(genreIds), params);
        }
    }

    private void saveFilmDirectors(final long id, final Collection<Director> directors) {
        if (directors != null && !directors.isEmpty()) {
            SqlParameterSource[] params = directors.stream()
                    .map(director -> new MapSqlParameterSource()
                            .addValue("id", id)
                            .addValue("directorId", director.getId())
                    )
                    .toArray(SqlParameterSource[]::new);
            jdbc.batchUpdate(SAVE_FILM_DIRECTOR_QUERY, params);
        }
    }

    private void updateFilmDirectors(final long id, final Collection<Director> directors) {
        saveFilmDirectors(id, directors);
        deleteFilmDirectorsExcept(id, directors);
    }

    private void deleteFilmDirectorsExcept(final long id, final Collection<Director> directors) {
        var params = new MapSqlParameterSource("id", id);
        if (directors == null || directors.isEmpty()) {
            execute(DELETE_ALL_FILM_DIRECTORS_QUERY, params);
        } else {
            final String directorIds = directors.stream()
                    .map(Director::getId)
                    .map(Objects::toString)
                    .collect(Collectors.joining(", "));
            execute(DELETE_FILM_DIRECTORS_QUERY.formatted(directorIds), params);
        }
    }
}
