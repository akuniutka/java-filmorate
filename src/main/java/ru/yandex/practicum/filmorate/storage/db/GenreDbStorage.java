package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = :id;";
    private static final String FIND_ALL_BY_FILM_ID_QUERY = """
            SELECT g.*
            FROM genres AS g
            JOIN film_genres AS fg ON g.genre_id = fg.genre_id
            WHERE fg.film_id = :filmId
            ORDER BY g.genre_id;
            """;
    private static final String FIND_ALL_BY_FILM_IDS_QUERY = """
            SELECT fg.film_id,
              g.*
            FROM genres AS g
            JOIN film_genres AS fg ON g.genre_id = fg.genre_id
            WHERE fg.film_id IN (%s);
            """;
    private static final String SAVE_FILM_GENRE_QUERY = """
            INSERT INTO film_genres (film_id, genre_id)
            VALUES (:filmId, :genreId);
            """;
    private static final String DELETE_FILM_GENRE_QUERY = """
            DELETE FROM film_genres
            WHERE film_id = :filmId AND genre_id = :genreId;
            """;

    @Autowired
    public GenreDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    @Override
    public Collection<Genre> findAllByFilmId(Long filmId) {
        var params = new MapSqlParameterSource("filmId", filmId);
        return findMany(FIND_ALL_BY_FILM_ID_QUERY, params);
    }

    @Override
    public Map<Long, Collection<Genre>> findAllByFilmId(Set<Long> filmIds) {
        final String queryParams = filmIds.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", "));
        final Map<Long, Collection<Genre>> result = new HashMap<>();
        jdbc.getJdbcOperations().query(FIND_ALL_BY_FILM_IDS_QUERY.formatted(queryParams),
                rs -> {
                    Long filmId = rs.getLong("film_id");
                    Genre genre = mapper.mapRow(rs, 0);
                    result.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
                }
        );
        return result;
    }

    @Override
    public Optional<Genre> findById(final Long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void saveFilmGenre(Long filmId, Genre genre) {
        var params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("genreId", genre.getId());
        execute(SAVE_FILM_GENRE_QUERY, params);
    }

    @Override
    public void deleteFilmGenre(Long filmId, Genre genre) {
        var params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("genreId", genre.getId());
        execute(DELETE_FILM_GENRE_QUERY, params);
    }
}