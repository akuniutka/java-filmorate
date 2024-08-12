package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.FriendStorage;

import java.util.Collection;

@Repository
public class FriendDbStorage extends BaseDbStorage<User> implements FriendStorage {

    private static final String SAVE_QUERY = """
            MERGE INTO friends
            KEY (user_id, friend_id)
            VALUES (:userId, :friendId, NULL);
            """;
    private static final String DELETE_QUERY = """
            DELETE FROM friends
            WHERE user_id = :userId AND friend_id = :friendId;
            """;
    private static final String FIND_ALL_BY_USER_ID_QUERY = """
            SELECT u.*
            FROM users AS u
            JOIN friends AS f ON u.user_id = f.friend_id
            WHERE f.user_id = :userId
            ORDER BY f.friend_id;
            """;
    private static final String FIND_COMMON_FRIENDS_QUERY = """
            SELECT u.*
            FROM friends AS f1
            JOIN friends AS f2
            ON f1.friend_id = f2.friend_id
            JOIN users AS u ON f1.friend_id = u.user_id
            WHERE f1.user_id = :userId AND f2.user_id = :friendId
            ORDER BY f1.friend_id;
            """;

    @Autowired
    public FriendDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void save(final Long userId, final User friend) {
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friend.getId());
        execute(SAVE_QUERY, params);
    }

    @Override
    public void delete(final Long userId, final User friend) {
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friend.getId());
        execute(DELETE_QUERY, params);
    }

    @Override
    public Collection<User> findAllByUserId(final Long userId) {
        var params = new MapSqlParameterSource("userId", userId);
        return findMany(FIND_ALL_BY_USER_ID_QUERY, params);
    }

    @Override
    public Collection<User> findAllCommonFriends(final Long userId, final Long friendId) {
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendId);
        return findMany(FIND_COMMON_FRIENDS_QUERY, params);
    }
}
