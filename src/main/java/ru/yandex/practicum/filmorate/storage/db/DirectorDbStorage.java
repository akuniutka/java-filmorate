package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.api.DirectorStorage;

import java.util.Optional;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

    private static final String SAVE_QUERY = """
            SELECT * FROM FINAL TABLE (
              INSERT INTO directors (director_name)
              VALUES (:name)
            );
            """;
    private static final String UPDATE_QUERY = """
            SELECT * FROM FINAL TABLE (
              UPDATE directors
              SET director_name = :name
              WHERE director_id = :id
            );
            """;

    public DirectorDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<Director> mapper) {
        super(Director.class, jdbc, mapper);
    }

    @Override
    public Director save(final Director director) {
        return save(SAVE_QUERY, director);
    }

    @Override
    public Optional<Director> update(final Director director) {
        return update(UPDATE_QUERY, director);
    }
}
