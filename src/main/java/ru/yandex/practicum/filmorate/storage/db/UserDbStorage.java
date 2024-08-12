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
    private static final String DELETE_QUERY = "DELETE FROM users WHERE user_id = :id;";
    private static final String DELETE_ALL_QUERY = "DELETE FROM users;";

    @Autowired
    public UserDbStorage(final NamedParameterJdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> findById(final Long id) {
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
    public void delete(final Long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public void deleteAll() {
        execute(DELETE_ALL_QUERY);
    }
}
