package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.storage.AbstractGenreStorageTest;

@JdbcTest
class GenreDbStorageIT extends AbstractGenreStorageTest {

    @Autowired
    GenreDbStorageIT(final NamedParameterJdbcTemplate jdbc) {
        this.genreStorage = new GenreDbStorage(jdbc);
    }
}
