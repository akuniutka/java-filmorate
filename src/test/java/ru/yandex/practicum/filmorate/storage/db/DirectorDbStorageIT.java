package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.storage.AbstractDirectorStorageTest;

@JdbcTest
public class DirectorDbStorageIT extends AbstractDirectorStorageTest {

    @Autowired
    DirectorDbStorageIT(final NamedParameterJdbcTemplate jdbc) {
        this.directorStorage = new DirectorDbStorage(jdbc);
    }
}
