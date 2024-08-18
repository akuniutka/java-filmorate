package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = :id;";

    @Autowired
    public GenreDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Genre> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }
}
