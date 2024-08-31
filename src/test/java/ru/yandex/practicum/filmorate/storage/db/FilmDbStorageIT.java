package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.storage.AbstractFilmStorageTest;

@JdbcTest
class FilmDbStorageIT extends AbstractFilmStorageTest {

    @Autowired
    FilmDbStorageIT(final NamedParameterJdbcTemplate jdbc) {
        this.filmStorage = new FilmDbStorage(jdbc);
    }
}
