package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BaseDbStorage<T> {

    protected static final String FIND_ALL_QUERY = "SELECT * FROM %s ORDER BY %s;";
    protected static final String FIND_BY_ID_QUERY = "SELECT * FROM %s WHERE %s = :id;";
    protected static final String FIND_BY_IDS_QUERY = "SELECT * FROM %s WHERE %s IN (%s);";
    protected static final String DELETE_QUERY = "DELETE FROM %s WHERE %S = :id;";
    protected static final String DELETE_ALL_QUERY = "DELETE FROM %s;";

    protected final NamedParameterJdbcTemplate jdbc;
    protected final RowMapper<T> mapper;
    protected final String tableName;
    protected String keyName;

    protected BaseDbStorage(Class<T> entityType, NamedParameterJdbcTemplate jdbc, RowMapper<T> mapper) {
        String baseName = entityType.getSimpleName().toLowerCase();
        this.tableName = baseName + "s";
        this.keyName = baseName + "_id";
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    protected BaseDbStorage(Class<T> entityType, NamedParameterJdbcTemplate jdbc) {
        this(entityType, jdbc, new BeanPropertyRowMapper<>(entityType));
    }

    public Collection<T> findAll() {
        return jdbc.query(FIND_ALL_QUERY.formatted(tableName, keyName), mapper);
    }

    public Optional<T> findById(final long id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        return findOne(FIND_BY_ID_QUERY.formatted(tableName, keyName), params);
    }

    public Collection<T> findById(final Collection<Long> ids) {
        String idsStr = ids.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        return jdbc.query(FIND_BY_IDS_QUERY.formatted(tableName, keyName, idsStr), mapper);
    }

    public T save(String query, T entity) {
        return update(query, entity).orElseThrow();
    }

    public Optional<T> update(String query, T entity) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(entity);
        return findOne(query, params);
    }

    public boolean delete(final long id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.update(DELETE_QUERY.formatted(tableName, keyName), params) > 0;
    }

    public void deleteAll() {
        jdbc.update(DELETE_ALL_QUERY.formatted(tableName), Collections.emptyMap());
    }

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
