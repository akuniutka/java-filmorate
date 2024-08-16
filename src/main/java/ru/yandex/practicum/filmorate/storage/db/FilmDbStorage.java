package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            ORDER BY film_id;
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM films AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
            WHERE film_id = :id;
            """;
    private static final String SAVE_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM (
              SELECT *
              FROM FINAL TABLE (
                INSERT INTO films (film_name, description, release_date, duration, mpa_id)
                VALUES (:name, :description, :releaseDate, :duration, :mpaId)
              )
            ) AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id;
            """;
    private static final String UPDATE_QUERY = """
            SELECT f.*,
              m.mpa_name
            FROM (
              SELECT *
              FROM FINAL TABLE (
                UPDATE films
                SET film_name = :name,
                  description = :description,
                  release_date = :releaseDate,
                  duration = :duration,
                  mpa_id = :mpaId
                WHERE film_id = :id
              )
            ) AS f
            LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id;
            """;
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = :id;";
    private static final String DELETE_ALL_QUERY = "DELETE FROM films;";

    @Autowired
    public FilmDbStorage(final NamedParameterJdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Film> findById(final Long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Film save(final Film film) {
        Long mpaId = film.getMpa() == null ? null : film.getMpa().getId();
        var params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                // Use string representation due to bug with dates before 1900-01-02
                .addValue("releaseDate", film.getReleaseDate().toString())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", mpaId);
        return findOne(SAVE_QUERY, params).orElseThrow();
    }

    @Override
    public Optional<Film> update(final Film film) {
        Long mpaId = film.getMpa() == null ? null : film.getMpa().getId();
        var params = new MapSqlParameterSource()
                .addValue("id", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                // Use string representation due to bug with dates before 1900-01-02
                .addValue("releaseDate", film.getReleaseDate().toString())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", mpaId);
        return findOne(UPDATE_QUERY, params);
    }

    @Override
    public void delete(final Long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public void deleteAll() {
        execute(DELETE_ALL_QUERY);
    }
}
