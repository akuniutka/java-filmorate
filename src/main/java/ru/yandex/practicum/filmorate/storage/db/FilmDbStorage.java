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

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String GET_LIKES_BY_USER_ID_QUERY = """
        SELECT film_id
        FROM likes
        WHERE user_id = :userId;
    """;

    private static final String FIND_ALL_QUERY = """
            SELECT f.*
            FROM films AS f
            ORDER BY film_id;
            """;
    private static final String FIND_ALL_ORDER_BY_LIKES_DESC = """
            SELECT f.*
            FROM films AS f
            JOIN film_ratings AS fr ON f.film_id = fr.film_id
            ORDER BY fr.rating DESC, f.film_id
            LIMIT :limit;
            """;
    private static final String FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_GENRE_AND_YEAR = """
            SELECT f.*
            FROM films AS f
            JOIN film_genres fg ON f.film_id = fg.film_id
            JOIN film_ratings fr ON f.film_id = fr.film_id
            WHERE fg.genre_id = :genreId
              AND EXTRACT (YEAR FROM release_date) = :year
            ORDER BY fr.rating DESC, f.film_id
            LIMIT :limit;
            """;
    private static final String FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_GENRE = """
            SELECT f.*
            FROM films AS f
            JOIN film_genres fg ON f.film_id = fg.film_id
            JOIN film_ratings AS fr ON f.film_id = fr.film_id
            WHERE fg.genre_id = :genreId
            ORDER BY fr.rating DESC, film_id
            LIMIT :limit;
            """;
    private static final String FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_YEAR = """
            SELECT f.*
            FROM films AS f
            JOIN film_ratings AS fr ON f.film_id = fr.film_id
            WHERE EXTRACT (YEAR FROM release_date) = :year
            ORDER BY fr.rating DESC, f.film_id
            LIMIT :limit;
            """;
    private static final String FIND_ALL_BY_DIRECTOR_ID_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN film_directors AS fd ON f.film_id = fd.film_id
            WHERE fd.director_id = :directorId
            ORDER BY f.film_id;
            """;
    private static final String FIND_ALL_BY_DIRECTOR_ID_ORDER_BY_YEAR_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN film_directors AS fd ON f.film_id = fd.film_id
            WHERE fd.director_id = :directorId
            ORDER BY EXTRACT(YEAR FROM f.release_date), f.film_id;
            """;
    private static final String FIND_ALL_BY_DIRECTOR_ID_ORDER_BY_LIKES_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN film_directors AS fd ON f.film_id = fd.film_id
            JOIN film_ratings AS fr ON fr.film_id = f.film_id
            WHERE fd.director_id = :directorId
            ORDER BY fr.rating DESC, f.film_id;
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT f.*
            FROM films AS f
            WHERE film_id = :id;
            """;
    private static final String SAVE_QUERY = """
            SELECT *
            FROM (
              SELECT *
              FROM FINAL TABLE (
                INSERT INTO films (film_name, description, release_date, duration, mpa_id)
                VALUES (:name, :description, :releaseDate, :duration, :mpaId)
              )
            );
            """;
    private static final String UPDATE_QUERY = """
            SELECT *
            FROM (
              SELECT *
              FROM FINAL TABLE (
                UPDATE films
                SET film_name = :name,
                  description = :description,
                  release_date = :releaseDate,
                  duration = :duration,
                  mpa_id = :mpaId
                WHERE film_id = :id
              )
            );
            """;
    private static final String ADD_LIKE_QUERY = """
            MERGE INTO likes
            KEY (film_id, user_id)
            VALUES (:id, :userId);
            """;
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM likes
            WHERE film_id = :id AND user_id = :userId;
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
    private static final String SAVE_FILM_GENRE_QUERY = """
            MERGE INTO film_genres
            KEY (film_id, genre_id)
            VALUES (:id, :genreId);
            """;
    private static final String DELETE_FILM_GENRES_QUERY = """
            DELETE FROM film_genres
            WHERE film_id = :id AND genre_id NOT IN (%s);
            """;
    private static final String DELETE_ALL_FILM_GENRES_QUERY = """
            DELETE FROM film_genres
            WHERE film_id = :id;
            """;
    private static final String FIND_FILM_DIRECTORS_QUERY = """
            SELECT d.*
            FROM directors AS d
            JOIN film_directors AS fd ON d.id = fd.director_id
            WHERE fd.film_id = :id
            ORDER BY d.id
            """;
    private static final String FIND_FILMS_DIRECTORS = """
            SELECT fd.film_id,
              d.*
            FROM directors AS d
            JOIN film_directors AS fd ON d.id = fd.director_id
            WHERE fd.film_id IN (%s)
            ORDER BY d.id;
            """;
    private static final String SAVE_FILM_DIRECTOR_QUERY = """
            MERGE INTO film_directors
            KEY (film_id, director_id)
            VALUES (:id, :directorId);
            """;
    private static final String DELETE_FILM_DIRECTORS_QUERY = """
            DELETE FROM film_directors
            WHERE film_id = :id AND director_id NOT IN (%s);
            """;
    private static final String DELETE_ALL_FILM_DIRECTORS_QUERY = """
            DELETE FROM film_directors
            WHERE film_id = :id;
            """;

    private static final String SEARCH_FILMS_BY_TITLE_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN film_ratings AS fr ON f.film_id = fr.film_id
            WHERE film_name ILIKE :query
            ORDER BY fr.rating DESC, f.film_id;
            """;

    private static final String SEARCH_FILMS_BY_DIRECTORY_NAME_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN film_ratings AS fr ON f.film_id = fr.film_id
            WHERE f.film_id IN
            (
              SELECT fd.film_id
              FROM film_directors AS fd
              JOIN directors AS d ON fd.director_id = d.id
              WHERE d.name ILIKE :query
            )
            ORDER BY fr.rating DESC, f.film_id;
            """;

    private static final String SEARCH_FILMS_BY_TITLE_AND_DIRECTORY_NAME_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN film_ratings AS fr ON f.film_id = fr.film_id
            LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.id
            WHERE f.film_name ILIKE :query OR f.film_id IN
            (
              SELECT fd.film_id
              FROM film_directors AS fd
              JOIN directors AS d ON fd.director_id = d.id
              WHERE d.name ILIKE :query
            )
            ORDER BY fr.rating DESC, f.film_id;
            """;

    private static final String FIND_COMMON_FILMS_QUERY = """
            SELECT f.*
            FROM films AS f
            JOIN likes AS l1 ON f.film_id = l1.film_id
            JOIN likes AS l2 ON f.film_id = l2.film_id
            JOIN film_ratings AS fr ON f.film_id = fr.film_id
            WHERE l1.user_id = :id AND l2.user_id = :friendId
            ORDER BY fr.rating DESC, f.film_id;
            """;
    private static final String FIND_FILM_MPA_QUERY = """
            SELECT m.*
            FROM mpas AS m
            JOIN films AS f ON m.id = f.mpa_id
            WHERE f.film_id = :id
            """;
    private static final String FIND_FILMS_MPA_QUERY = """
            SELECT f.film_id,
              m.*
            FROM mpas AS m
            JOIN films AS f ON m.id = f.mpa_id
            WHERE f.film_id IN (%s);
            """;

    private final RowMapper<Mpa> mpaMapper;
    private final RowMapper<Genre> genreMapper;
    private final RowMapper<Director> directorMapper;

    @Autowired
    public FilmDbStorage(
            final NamedParameterJdbcTemplate jdbc,
            final RowMapper<Film> mapper) {
        super(Film.class, jdbc, mapper);
        this.mpaMapper = new BeanPropertyRowMapper<>(Mpa.class);
        this.genreMapper = new BeanPropertyRowMapper<>(Genre.class);
        this.directorMapper = new BeanPropertyRowMapper<>(Director.class);
    }

    @Override
    public Collection<Film> findAll() {
        return fetchCollections(findAll(FIND_ALL_QUERY));
    }

    @Override
    public Collection<Film> findAllOrderByLikesDesc(long limit, Long genreId, Integer year) {
        SqlParameterSource params;
        String query;
        if (genreId != 0 && year != 0) {
            params = new MapSqlParameterSource()
                    .addValue("limit", limit)
                    .addValue("genreId", genreId)
                    .addValue("year", year);
            query = FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_GENRE_AND_YEAR;
        } else if (genreId != 0) {
            params = new MapSqlParameterSource()
                    .addValue("limit", limit)
                    .addValue("genreId", genreId);
            query = FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_GENRE;
        } else if (year != 0) {
            params = new MapSqlParameterSource()
                    .addValue("limit", limit)
                    .addValue("year", year);
            query = FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_YEAR;
        } else {
            params = new MapSqlParameterSource("limit", limit);
            query = FIND_ALL_ORDER_BY_LIKES_DESC;
        }
        return fetchCollections(findMany(query, params));
    }

    @Override
    public Collection<Film> findAllByDirectorId(final long directorId) {
        var params = new MapSqlParameterSource("directorId", directorId);
        return fetchCollections(findMany(FIND_ALL_BY_DIRECTOR_ID_QUERY, params));
    }

    @Override
    public Collection<Film> findAllByDirectorIdOrderByYear(final long directorId) {
        var params = new MapSqlParameterSource("directorId", directorId);
        return fetchCollections(findMany(FIND_ALL_BY_DIRECTOR_ID_ORDER_BY_YEAR_QUERY, params));
    }

    @Override
    public Collection<Film> findAllByDirectorIdOrderByLikes(final long directorId) {
        var params = new MapSqlParameterSource("directorId", directorId);
        return fetchCollections(findMany(FIND_ALL_BY_DIRECTOR_ID_ORDER_BY_LIKES_QUERY, params));
    }

    @Override
    public Optional<Film> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id).map(this::fetchCollections);
    }

    @Override
    public Film save(final Film film) {
        Long mpaId = film.getMpa() == null ? null : film.getMpa().getId();
        var params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                // Use string representation due to bug with dates before 1900-01-02
                .addValue("releaseDate", film.getReleaseDate().toString())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", mpaId);
        Film savedFilm = findOne(SAVE_QUERY, params).orElseThrow();
        saveFilmGenres(savedFilm.getId(), film.getGenres());
        saveFilmDirectors(savedFilm.getId(), film.getDirectors());
        return fetchCollections(savedFilm);
    }

    @Override
    public Optional<Film> update(final Film film) {
        Long mpaId = film.getMpa() == null ? null : film.getMpa().getId();
        var params = new MapSqlParameterSource()
                .addValue("id", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                // Use string representation due to bug with dates before 1900-01-02
                .addValue("releaseDate", film.getReleaseDate().toString())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", mpaId);
        Optional<Film> savedFilm = findOne(UPDATE_QUERY, params);
        savedFilm.ifPresent(f -> {
            saveFilmGenres(film.getId(), film.getGenres());
            deleteFilmGenresExcept(film.getId(), film.getGenres());
            saveFilmDirectors(film.getId(), film.getDirectors());
            deleteFilmDirectorsExcept(film.getId(), film.getDirectors());
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
        return jdbc.update(DELETE_LIKE_QUERY, params) > 0;
    }

    @Override
    public Collection<Film> searchFilmsByTitle(String query) {
        String searchQuery = "%" + query + "%";
        var params = new MapSqlParameterSource("query", searchQuery);
        return fetchCollections(findMany(SEARCH_FILMS_BY_TITLE_QUERY, params));
    }

    @Override
    public Collection<Film> searchFilmsByDirectorName(String query) {
        String searchQuery = "%" + query + "%";
        var params = new MapSqlParameterSource("query", searchQuery);
        return fetchCollections(findMany(SEARCH_FILMS_BY_DIRECTORY_NAME_QUERY, params));
    }

    @Override
    public Collection<Film> searchFilmsByTitleAndDirectorName(String query) {
        String searchQuery = "%" + query + "%";
        var params = new MapSqlParameterSource("query", searchQuery);
        return fetchCollections(findMany(SEARCH_FILMS_BY_TITLE_AND_DIRECTORY_NAME_QUERY, params));
    }

    @Override
    public Collection<Film> getCommonFilms(long id, long friendId) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("friendId", friendId);
        return fetchCollections(findMany(FIND_COMMON_FILMS_QUERY, params));
    }

    private Film fetchCollections(final Film film) {
        return Optional.of(film)
                .map(this::fetchMpa)
                .map(this::supplementWithGenres)
                .map(this::fetchDirectors)
                .orElseThrow();
    }

    private Collection<Film> fetchCollections(final Collection<Film> films) {
        return Optional.of(films)
                .map(this::fetchMpa)
                .map(this::supplementWithGenres)
                .map(this::fetchDirectors)
                .orElseThrow();
    }

    private Film fetchMpa(final Film film) {
        SqlParameterSource params = new MapSqlParameterSource("id", film.getId());
        try {
            Mpa mpa = jdbc.queryForObject(FIND_FILM_MPA_QUERY, params, mpaMapper);
            film.setMpa(mpa);
        } catch (EmptyResultDataAccessException ignored) {
            film.setMpa(null);
        }
        return film;
    }

    private Collection<Film> fetchMpa(final Collection<Film> films) {
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

    private Film supplementWithGenres(final Film film) {
        var params = new MapSqlParameterSource("id", film.getId());
        Collection<Genre> genres = jdbc.query(FIND_FILM_GENRES_QUERY, params, genreMapper);
        film.setGenres(genres);
        return film;
    }

    private Collection<Film> supplementWithGenres(final Collection<Film> films) {
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

    private void saveFilmGenres(final long id, final Collection<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            SqlParameterSource[] params = genres.stream()
                    .map(genre -> new MapSqlParameterSource()
                            .addValue("id", id)
                            .addValue("genreId", genre.getId())
                    )
                    .toArray(SqlParameterSource[]::new);
            jdbc.batchUpdate(SAVE_FILM_GENRE_QUERY, params);
        }
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

    private Film fetchDirectors(final Film film) {
        var params = new MapSqlParameterSource("id", film.getId());
        Collection<Director> directors = jdbc.query(FIND_FILM_DIRECTORS_QUERY, params, directorMapper);
        film.setDirectors(directors);
        return film;
    }

    private Collection<Film> fetchDirectors(final Collection<Film> films) {
        final String filmIds = films.stream()
                .map(Film::getId)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        final Map<Long, Collection<Director>> directorsByFilmId = new HashMap<>();
        jdbc.getJdbcOperations().query(FIND_FILMS_DIRECTORS.formatted(filmIds),
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

    @Override
    public Set<Long> getLikesByUserId(long userId) {
        var params = new MapSqlParameterSource("userId", userId);
        return new HashSet<>(jdbc.query(GET_LIKES_BY_USER_ID_QUERY, params, (rs, rowNum) -> rs.getLong("film_id")));
    }
}
