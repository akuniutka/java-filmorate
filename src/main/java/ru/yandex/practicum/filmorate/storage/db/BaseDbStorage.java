package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseDbStorage<T> {

    protected final NamedParameterJdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected List<T> findAll(final String query) {
        return jdbc.query(query, mapper);
    }

    protected Optional<T> findById(final String query, final long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(query, Map.of("id", id), mapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected void delete(final String query, final long id) {
        jdbc.update(query, Map.of("id", id));
    }

    protected void execute(final String query) {
        jdbc.update(query, Collections.emptyMap());
    }

    protected void execute(final String query, final SqlParameterSource params) {
        jdbc.update(query, params);
    }

    protected Optional<T> findOne(final String query, final SqlParameterSource params) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(query, params, mapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(final String query, final SqlParameterSource params) {
        return jdbc.query(query, params, mapper);
    }
}
