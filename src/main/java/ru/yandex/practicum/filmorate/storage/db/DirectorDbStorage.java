package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.api.DirectorStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM directors ORDER BY director_id;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE director_id = :id;";
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
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE director_id = :id;";
    private static final String DELETE_ALL_QUERY = "DELETE FROM directors;";

    public DirectorDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Director> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Director> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Director save(final Director director) {
        var params = new MapSqlParameterSource("name", director.getName());
        return findOne(SAVE_QUERY, params).orElseThrow();
    }

    @Override
    public Optional<Director> update(final Director director) {
        var params = new MapSqlParameterSource()
                .addValue("id", director.getId())
                .addValue("name", director.getName());
        return findOne(UPDATE_QUERY, params);
    }

    @Override
    public void delete(final long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public void deleteAll() {
        execute(DELETE_ALL_QUERY);
    }
}
