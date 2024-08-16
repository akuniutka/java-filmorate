package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.storage.AbstractUserStorageTest;
import ru.yandex.practicum.filmorate.storage.db.mapper.UserRowMapper;

@JdbcTest
class UserDbStorageIT extends AbstractUserStorageTest {

    @Autowired
    UserDbStorageIT(final NamedParameterJdbcTemplate jdbc) {
        this.userStorage = new UserDbStorage(jdbc, new UserRowMapper());
    }
}