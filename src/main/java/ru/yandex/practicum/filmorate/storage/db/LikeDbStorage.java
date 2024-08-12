package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.api.LikeStorage;

import java.util.Collection;

@Repository
public class LikeDbStorage extends BaseDbStorage<Film> implements LikeStorage {

    private static final String SAVE_QUERY = """
            MERGE INTO likes
            KEY (film_id, user_id)
            VALUES (:filmId, :userId);
            """;
    private static final String DELETE_QUERY = """
            DELETE FROM likes
            WHERE film_id = :filmId AND user_id = :userId;
            """;
    private static final String FIND_TOP_LIKED_QUERY = """
            SELECT film_id
            FROM likes
            GROUP BY film_id
            ORDER BY COUNT(user_id) DESC
            LIMIT :limit;
            """;

    @Autowired
    public LikeDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void save(final Long filmId, final Long userId) {
        var params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);
        execute(SAVE_QUERY, params);
    }

    @Override
    public void delete(final Long filmId, final Long userId) {
        var params = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);
        execute(DELETE_QUERY, params);
    }

    @Override
    public Collection<Long> findAllFilmIdOrderByLikesDesc(final Long limit) {
        var params = new MapSqlParameterSource("limit", limit);
        return jdbc.queryForList(FIND_TOP_LIKED_QUERY, params, Long.class);
    }
}
