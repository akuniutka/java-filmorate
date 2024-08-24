package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            ORDER BY film_id;
            """;
    private static final String FIND_ALL_ORDER_BY_LIKES_DESC = """
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN
            (
              SELECT film_id,
                COUNT(*) AS likes
              FROM likes
              GROUP BY film_id
            ) AS l ON f.film_id = l.film_id
            ORDER BY COALESCE (l.likes, 0) DESC
            LIMIT :limit
            """;
    private static final String FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_GENRE_AND_YEAR = """
            SELECT f.*,
              m.mpa_name,
              fg.genre_id
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN
            (
              SELECT film_id,
                COUNT(*) AS likes
              FROM likes
              GROUP BY film_id
            ) AS l ON f.film_id = l.film_id
            WHERE genre_Id = :genreId
            AND EXTRACT (YEAR FROM release_date) = :year
            ORDER BY COALESCE (l.likes, 0) DESC
            LIMIT :limit
            """;
    private static final String FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_GENRE = """
            SELECT f.*,
              m.mpa_name,
              fg.genre_id
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN
            (
              SELECT film_id,
                COUNT(*) AS likes
              FROM likes
              GROUP BY film_id
            ) AS l ON f.film_id = l.film_id
            WHERE genre_Id = :genreId
            ORDER BY COALESCE (l.likes, 0) DESC
            LIMIT :limit
            """;
    private static final String FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_YEAR = """
            SELECT f.*,
              m.mpa_name,
              fg.genre_id
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            LEFT JOIN
            (
              SELECT film_id,
                COUNT(*) AS likes
              FROM likes
              GROUP BY film_id
            ) AS l ON f.film_id = l.film_id
            WHERE EXTRACT (YEAR FROM release_date) = :year
            ORDER BY COALESCE (l.likes, 0) DESC
            LIMIT :limit
            """;
    private static final String FIND_ALL_BY_DIRECTOR_ID_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            WHERE fd.director_id = :directorId
            ORDER BY f.film_id;
            """;
    private static final String FIND_ALL_BY_DIRECTOR_ID_ORDER_BY_YEAR_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            WHERE fd.director_id = :directorId
            ORDER BY EXTRACT(YEAR FROM f.release_date), f.film_id;
            """;
    private static final String FIND_ALL_BY_DIRECTOR_ID_ORDER_BY_LIKES_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN
            (
              SELECT film_id,
                COUNT(*) AS likes
              FROM likes
              GROUP BY film_id
            ) AS l ON f.film_id = l.film_id
            WHERE fd.director_id = :directorId
            ORDER BY COALESCE (l.likes, 0) DESC, f.film_id;
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            WHERE film_id = :id;
            """;
    private static final String SAVE_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM (
              SELECT *
              FROM FINAL TABLE (
                INSERT INTO films (film_name, description, release_date, duration, mpa_id)
                VALUES (:name, :description, :releaseDate, :duration, :mpaId)
              )
            ) AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id;
            """;
    private static final String UPDATE_QUERY = """
            SELECT f.*,
              m.mpa_name
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
            ) AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id;
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
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = :id;";
    private static final String DELETE_ALL_QUERY = "DELETE FROM films;";
    private static final String FIND_GENRES_BY_FILM_ID_QUERY = """
            SELECT g.*
            FROM genres AS g
            JOIN film_genres AS fg ON g.genre_id = fg.genre_id
            WHERE fg.film_id = :id
            ORDER BY g.genre_id;
            """;
    private static final String FIND_GENRES_BY_FILM_IDS_QUERY = """
            SELECT fg.film_id,
              g.*
            FROM genres AS g
            JOIN film_genres AS fg ON g.genre_id = fg.genre_id
            WHERE fg.film_id IN (%s)
            ORDER BY g.genre_id;
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
    private static final String FIND_DIRECTORS_BY_FILM_ID_QUERY = """
            SELECT d.*
            FROM directors AS d
            JOIN film_directors AS fd ON d.director_id = fd.director_id
            WHERE fd.film_id = :id
            ORDER BY d.director_id
            """;
    private static final String FIND_DIRECTORS_BY_FILM_IDS_QUERY = """
            SELECT fd.film_id,
              d.*
            FROM directors AS d
            JOIN film_directors AS fd ON d.director_id = fd.director_id
            WHERE fd.film_id IN (%s)
            ORDER BY d.director_id;
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
            SELECT f.*,
              m.mpa_name,
              COUNT(l.film_id) AS like_count
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN likes AS l ON f.film_id = l.film_id
            WHERE film_name ILIKE :query
            GROUP BY f.film_id, m.mpa_name
            ORDER BY like_count DESC
            """;

    private static final String SEARCH_FILMS_BY_DIRECTORY_NAME_QUERY = """
            SELECT f.*,
              m.mpa_name,
              COUNT(l.film_id) AS like_count
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN likes AS l ON f.film_id = l.film_id
            LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.director_id
            WHERE d.director_name ILIKE :query
            GROUP BY f.film_id, m.mpa_name
            ORDER BY like_count DESC
            """;

    private static final String SEARCH_FILMS_BY_TITLE_AND_DIRECTORY_NAME_QUERY = """
            SELECT f.*,
               m.mpa_name,
               COUNT(DISTINCT l.film_id) AS like_count
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            LEFT JOIN likes AS l ON f.film_id = l.film_id
            LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.director_id
            WHERE (f.film_name ILIKE :query OR d.director_name ILIKE :query)
            GROUP BY f.film_id, m.mpa_name
            ORDER BY like_count DESC
            """;

    private final RowMapper<Genre> genreMapper;
    private final RowMapper<Director> directorMapper;

    @Autowired
    public FilmDbStorage(
            final NamedParameterJdbcTemplate jdbc,
            final RowMapper<Film> mapper,
            final RowMapper<Genre> genreMapper,
            final RowMapper<Director> directorMapper) {
        super(jdbc, mapper);
        this.genreMapper = genreMapper;
        this.directorMapper = directorMapper;
    }

    @Override
    public Collection<Film> findAll() {
        return supplementWithDirectors(supplementWithGenres(findAll(FIND_ALL_QUERY)));
    }

    @Override
    public Collection<Film> findAllOrderByLikesDesc(long limit, Long genreId, Integer year) {
        if (genreId != 0 && year != 0) {
            var params = new MapSqlParameterSource()
                    .addValue("limit", limit)
                    .addValue("genreId", genreId)
                    .addValue("year", year);
            return supplementWithGenres(findMany(FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_GENRE_AND_YEAR, params));
        } else if (genreId != 0 && year == 0) {
            var params = new MapSqlParameterSource()
                    .addValue("limit", limit)
                    .addValue("genreId", genreId);
            return supplementWithGenres(findMany(FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_GENRE, params));
        } else if (genreId == 0 && year != 0) {
            var params = new MapSqlParameterSource()
                    .addValue("limit", limit)
                    .addValue("year", year);
            return supplementWithGenres(findMany(FIND_ALL_ORDER_BY_LIKES_DESC_FILTER_BY_YEAR, params));
        }
        var params = new MapSqlParameterSource("limit", limit);
        return supplementWithDirectors(supplementWithGenres(findMany(FIND_ALL_ORDER_BY_LIKES_DESC, params)));
    }

    @Override
    public Collection<Film> findAllByDirectorId(final long directorId) {
        var params = new MapSqlParameterSource("directorId", directorId);
        return supplementWithDirectors(supplementWithGenres(findMany(FIND_ALL_BY_DIRECTOR_ID_QUERY, params)));
    }

    @Override
    public Collection<Film> findAllByDirectorIdOrderByYear(final long directorId) {
        var params = new MapSqlParameterSource("directorId", directorId);
        return supplementWithDirectors(supplementWithGenres(findMany(FIND_ALL_BY_DIRECTOR_ID_ORDER_BY_YEAR_QUERY, params)));
    }

    @Override
    public Collection<Film> findAllByDirectorIdOrderByLikes(final long directorId) {
        var params = new MapSqlParameterSource("directorId", directorId);
        return supplementWithDirectors(supplementWithGenres(findMany(FIND_ALL_BY_DIRECTOR_ID_ORDER_BY_LIKES_QUERY, params)));
    }

    @Override
    public Optional<Film> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id).map(this::supplementWithGenres).map(this::supplementWithDirectors);
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
        return supplementWithDirectors(supplementWithGenres(savedFilm));
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
        return savedFilm.map(this::supplementWithGenres).map(this::supplementWithDirectors);
    }

    @Override
    public void addLike(final long id, final long userId) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId);
        execute(ADD_LIKE_QUERY, params);
    }

    @Override
    public void deleteLike(final long id, final long userId) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId);
        execute(DELETE_LIKE_QUERY, params);
    }

    @Override
    public void delete(final long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public void deleteAll() {
        execute(DELETE_ALL_QUERY);
    }

    @Override
    public Collection<Film> searchFilmsByTitle(String query) {
        String searchQuery = "%" + query + "%";
        var params = new MapSqlParameterSource()
                .addValue("query", searchQuery);
        return supplementWithDirectors(supplementWithGenres(findMany(SEARCH_FILMS_BY_TITLE_QUERY, params)));
    }

    @Override
    public Collection<Film> searchFilmsByDirectorName(String query) {
        String searchQuery = "%" + query + "%";
        var params = new MapSqlParameterSource()
                .addValue("query", searchQuery);
        return supplementWithDirectors(supplementWithGenres(findMany(SEARCH_FILMS_BY_DIRECTORY_NAME_QUERY, params)));
    }

    @Override
    public Collection<Film> searchFilmsByTitleAndDirectorName(String query) {
        String searchQuery = "%" + query + "%";
        var params = new MapSqlParameterSource()
                .addValue("query", searchQuery);

        return supplementWithDirectors(supplementWithGenres(findMany(SEARCH_FILMS_BY_TITLE_AND_DIRECTORY_NAME_QUERY, params)));
    }

    private Film supplementWithGenres(final Film film) {
        var params = new MapSqlParameterSource("id", film.getId());
        Collection<Genre> genres = jdbc.query(FIND_GENRES_BY_FILM_ID_QUERY, params, genreMapper);
        film.setGenres(genres);
        return film;
    }

    private Collection<Film> supplementWithGenres(final Collection<Film> films) {
        final String filmIds = films.stream()
                .map(Film::getId)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        final Map<Long, Collection<Genre>> genresByFilmId = new HashMap<>();
        jdbc.getJdbcOperations().query(FIND_GENRES_BY_FILM_IDS_QUERY.formatted(filmIds),
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

    private Film supplementWithDirectors(final Film film) {
        var params = new MapSqlParameterSource("id", film.getId());
        Collection<Director> directors = jdbc.query(FIND_DIRECTORS_BY_FILM_ID_QUERY, params, directorMapper);
        film.setDirectors(directors);
        return film;
    }

    private Collection<Film> supplementWithDirectors(final Collection<Film> films) {
        final String filmIds = films.stream()
                .map(Film::getId)
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        final Map<Long, Collection<Director>> directorsByFilmId = new HashMap<>();
        jdbc.getJdbcOperations().query(FIND_DIRECTORS_BY_FILM_IDS_QUERY.formatted(filmIds),
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
