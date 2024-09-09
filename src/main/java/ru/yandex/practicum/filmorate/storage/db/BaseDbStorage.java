package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BaseDbStorage<T> {

    protected static final String SAVE_QUERY = "SELECT * FROM FINAL TABLE (INSERT INTO %s (%s) VALUES (%s));";
    protected static final String UPDATE_QUERY = "SELECT * FROM FINAL TABLE (UPDATE %s SET %s WHERE id = :id);";
    protected static final String FIND_ALL_QUERY = "SELECT * FROM %s %s %s %s;";
    protected static final String FIND_BY_ID_QUERY = "SELECT * FROM %s WHERE id = :id;";
    protected static final String FIND_BY_IDS_QUERY = "SELECT * FROM %s WHERE id IN (%s);";
    protected static final String DELETE_QUERY = "DELETE FROM %s WHERE id = :id;";
    protected static final String DELETE_ALL_QUERY = "DELETE FROM %s;";

    protected final NamedParameterJdbcTemplate jdbc;
    protected final RowMapper<T> mapper;
    protected final String tableName;

    protected BaseDbStorage(Class<T> entityType, NamedParameterJdbcTemplate jdbc, RowMapper<T> mapper) {
        this.tableName = entityType.getSimpleName().toLowerCase() + "s";
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    protected BaseDbStorage(Class<T> entityType, NamedParameterJdbcTemplate jdbc) {
        this(entityType, jdbc, new BeanPropertyRowMapper<>(entityType));
    }

    protected T save(final List<String> fields, final T entity) {
        final String dbFields = fields.stream()
                .map(this::toDbNotation)
                .collect(Collectors.joining(", "));
        final String entityFields = fields.stream()
                .map(fieldName -> ":" + fieldName)
                .collect(Collectors.joining(", "));
        final String query = SAVE_QUERY.formatted(tableName, dbFields, entityFields);
        return persist(query, entity).orElseThrow();
    }

    public Optional<T> findById(final long id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        return findOne(FIND_BY_ID_QUERY.formatted(tableName), params);
    }

    public Collection<T> findById(final Collection<Long> ids) {
        String idsStr = ids.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        return jdbc.query(FIND_BY_IDS_QUERY.formatted(tableName, idsStr), mapper);
    }

    public Collection<T> findAll() {
        return find(null, null, null);
    }

    protected Collection<T> findAll(final Long limit) {
        return find(null, null, limit);
    }

    protected Collection<T> findAll(final OrderBy orderBy) {
        return find(null, orderBy, null);
    }

    protected Collection<T> findAll(final OrderBy orderBy, final Long limit) {
        return find(null, orderBy, limit);
    }

    protected Collection<T> find(final Filter filter) {
        return find(filter, null, null);
    }

    protected Collection<T> find(final Filter filter, final Long limit) {
        return find(filter, null, limit);
    }

    protected Collection<T> find(final Filter filter, final OrderBy orderBy) {
        return find(filter, orderBy, null);
    }

    protected List<T> find(final Filter filter, final OrderBy orderBy, final Long limit) {
        final SqlParameterSource params;
        final String filterStr;
        if (filter == null) {
            filterStr = "";
            params = null;
        } else {
            filterStr = filter.getFilterString();
            params = filter.getFilterParams();
        }
        final String orderByStr;
        if (orderBy == null || orderBy.getEntries() == null) {
            orderByStr = "ORDER BY id";
        } else {
            orderByStr = "ORDER BY " + orderBy.getEntries().stream()
                    .map(entry -> toDbNotation(entry.field()) + " " + entry.order())
                    .collect(Collectors.joining(", "));
        }
        final String limitStr = limit != null ? "LIMIT " + limit : "";
        final String query = FIND_ALL_QUERY.formatted(tableName, filterStr, orderByStr, limitStr);
        return findMany(query, params);
    }

    protected Optional<T> update(final List<String> fields, final T entity) {
        String fieldPairs = fields.stream()
                .map(field -> toDbNotation(field) + " = :" + field)
                .collect(Collectors.joining(", "));
        final String query = UPDATE_QUERY.formatted(tableName, fieldPairs);
        return persist(query, entity);
    }

    public boolean delete(final long id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        return jdbc.update(DELETE_QUERY.formatted(tableName), params) > 0;
    }

    public void deleteAll() {
        jdbc.update(DELETE_ALL_QUERY.formatted(tableName), Collections.emptyMap());
    }

    protected Optional<T> persist(final String query, final T entity) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(entity) {
            @Override
            public Object getValue(@NonNull String paramName) throws IllegalArgumentException {
                Object value = super.getValue(paramName);
                if (value instanceof EventType) {
                    return value.toString();
                }
                if (value instanceof Operation) {
                    return value.toString();
                }
                return value;
            }
        };
        return findOne(query, params);
    }

    protected int execute(final String query, final SqlParameterSource params) {
        return jdbc.update(query, params);
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

    protected Filter and() {
        return new Filter();
    }

    protected Filter or() {
        return new Filter("OR");
    }

    protected OrderBy asc(final String field) {
        return new OrderBy().asc(field);
    }

    protected OrderBy desc(final String field) {
        return new OrderBy().desc(field);
    }

    private String toDbNotation(String fieldName) {
        StringBuilder sb = new StringBuilder();
        for (char ch : fieldName.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                sb.append('_').append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    protected class Filter {

        private final List<FilterEntry> entries;
        private final String combineOperator;
        private final MapSqlParameterSource params;
        private long lastParamNumber;

        Filter(final String combineOperator) {
            if (!"OR".equals(combineOperator) && !"AND".equals(combineOperator)) {
                throw new IllegalArgumentException("Combine operator should be AND or OR");
            }
            this.entries = new ArrayList<>();
            this.combineOperator = combineOperator;
            this.params = new MapSqlParameterSource();
            this.lastParamNumber = 0;
        }

        Filter() {
            this("AND");
        }

        public Filter eq(final String table, final String field, final Object value) {
            entries.add(new FilterEntry("=", table, field, value));
            return this;
        }

        public Filter eq(final String field, final Object value) {
            return eq(null, field, value);
        }

        public Filter like(final String table, final String field, final Object value) {
            entries.add(new FilterEntry("ILIKE", table, field, value));
            return this;
        }

        public Filter like(final String field, final Object value) {
            return like(null, field, value);
        }

        public String getFilterString() {
            if (entries.isEmpty()) {
                return "";
            }
            return "WHERE " + entries.stream()
                    .map(this::entryToString)
                    .collect(Collectors.joining(combineOperator));
        }

        public SqlParameterSource getFilterParams() {
            return params;
        }

        private String entryToString(final FilterEntry entry) {
            final String entryStr;
            final String param = "param" + lastParamNumber;
            params.addValue(param, entry.value());
            if (entry.table() == null) {
                entryStr = "(" + toDbNotation(entry.field()) + " " + entry.operator() + " :" + param + ")";
            } else {
                String joinedTable = entry.table();
                String baseName = tableName.substring(0, tableName.length() - 1);
                String baseTableJoinColumn = baseName + "_id";
                String joinTable = baseName + "_" + joinedTable;
                String joinedColumn = joinedTable.substring(0, joinedTable.length() - 1) + "_id";
                entryStr = """
                        (id IN
                        (
                          SELECT %s
                          FROM %s JOIN %s ON %s.%s = %s.id
                          WHERE %s.%s %s :%s
                        ))
                        """.formatted(baseTableJoinColumn, joinTable, joinedTable, joinTable, joinedColumn,
                        joinedTable, joinedTable, entry.field(), entry.operator(), param);
            }
            lastParamNumber++;
            return entryStr;
        }

        protected record FilterEntry(String operator, String table, String field, Object value) {
        }
    }

    protected static class OrderBy {

        private final List<OrderEntry> entries = new ArrayList<>();

        public OrderBy asc(final String field) {
            entries.add(new OrderEntry(field, "ASC"));
            return this;
        }

        public OrderBy desc(final String field) {
            entries.add(new OrderEntry(field, "DESC"));
            return this;
        }

        public List<OrderEntry> getEntries() {
            return new ArrayList<>(entries);
        }

        protected record OrderEntry(String field, String order) {
        }
    }
}
