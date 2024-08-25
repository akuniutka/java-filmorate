package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.storage.AbstractFilmStorageTest;
import ru.yandex.practicum.filmorate.storage.db.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.db.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.db.mapper.GenreRowMapper;

@JdbcTest
class FilmDbStorageIT extends AbstractFilmStorageTest {

    @Autowired
    FilmDbStorageIT(final NamedParameterJdbcTemplate jdbc) {
        this.filmStorage = new FilmDbStorage(jdbc, new FilmRowMapper(), new GenreRowMapper(), new DirectorRowMapper());
    }
}
