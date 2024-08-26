package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    @Autowired
    public GenreDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<Genre> mapper) {
        super(Genre.class, jdbc, mapper);
    }
}
