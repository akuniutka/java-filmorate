package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users ORDER BY user_id;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = :id;";
    private static final String SAVE_QUERY = """
            SELECT * FROM FINAL TABLE (
              INSERT INTO users (email, login, user_name, birthday)
              VALUES (:email, :login, :name, :birthday)
            );
            """;
    private static final String UPDATE_QUERY = """
            SELECT * FROM FINAL TABLE (
              UPDATE users
              SET email = :email,
                login = :login,
                user_name = :name,
                birthday = :birthday
              WHERE user_id = :id
            );
            """;
    private static final String ADD_FRIEND_QUERY = """
            MERGE INTO friends
            KEY (user_id, friend_id)
            VALUES (:id, :friendId, NULL);
            """;
    private static final String DELETE_FRIEND_QUERY = """
            DELETE FROM friends
            WHERE user_id = :id AND friend_id = :friendId;
            """;
    private static final String FIND_FRIENDS_QUERY = """
            SELECT u.*
            FROM users AS u
            JOIN friends AS f ON u.user_id = f.friend_id
            WHERE f.user_id = :id
            ORDER BY f.friend_id;
            """;
    private static final String FIND_COMMON_FRIENDS_QUERY = """
            SELECT u.*
            FROM friends AS f1
            JOIN friends AS f2
            ON f1.friend_id = f2.friend_id
            JOIN users AS u ON f1.friend_id = u.user_id
            WHERE f1.user_id = :id AND f2.user_id = :friendId
            ORDER BY f1.friend_id;
            """;
    private static final String DELETE_QUERY = "DELETE FROM users WHERE user_id = :id;";
    private static final String DELETE_ALL_QUERY = "DELETE FROM users;";

    @Autowired
    public UserDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }

    @Override
    public User save(final User user) {
        var params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", Date.valueOf(user.getBirthday()));
        return findOne(SAVE_QUERY, params).orElseThrow();
    }

    @Override
    public Optional<User> update(final User user) {
        var params = new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", Date.valueOf(user.getBirthday()));
        return findOne(UPDATE_QUERY, params);
    }

    @Override
    public void addFriend(final long id, final long friendId) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("friendId", friendId);
        execute(ADD_FRIEND_QUERY, params);
    }

    @Override
    public void deleteFriend(final long id, final long friendId) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("friendId", friendId);
        execute(DELETE_FRIEND_QUERY, params);
    }

    @Override
    public Collection<User> findFriends(final long id) {
        var params = new MapSqlParameterSource("id", id);
        return findMany(FIND_FRIENDS_QUERY, params);
    }

    @Override
    public Collection<User> findCommonFriends(final long id, final long friendId) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("friendId", friendId);
        return findMany(FIND_COMMON_FRIENDS_QUERY, params);
    }

    @Override
    public void delete(final long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public void deleteAll() {
        execute(DELETE_ALL_QUERY);
    }
}
