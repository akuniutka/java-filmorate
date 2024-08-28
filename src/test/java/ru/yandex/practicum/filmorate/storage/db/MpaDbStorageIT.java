package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.storage.AbstractMpaStorageTest;

@JdbcTest
class MpaDbStorageIT extends AbstractMpaStorageTest {

    @Autowired
    MpaDbStorageIT(final NamedParameterJdbcTemplate jdbc) {
        this.mpaStorage = new MpaDbStorage(jdbc);
    }
}