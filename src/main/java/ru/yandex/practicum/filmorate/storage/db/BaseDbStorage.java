package ru.yandex.practicum.filmorate.storage.db;

import lombok.Getter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    protected final String baseName;
    protected final String tableName;

    protected BaseDbStorage(Class<T> entityType, NamedParameterJdbcTemplate jdbc, RowMapper<T> mapper) {
        this.baseName = entityType.getSimpleName().toLowerCase();
        this.tableName = baseName + "s";
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    protected BaseDbStorage(final Class<T> entityType, final NamedParameterJdbcTemplate jdbc) {
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

    protected Collection<T> find(final Where where) {
        return find(where, null, null);
    }

    protected Collection<T> find(final Where where, final Long limit) {
        return find(where, null, limit);
    }

    protected Collection<T> find(final Where where, final OrderBy orderBy) {
        return find(where, orderBy, null);
    }

    protected List<T> find(final Where where, final OrderBy orderBy, final Long limit) {
        final String whereClause = where == null ? "" : "\n" + where.getCondition();
        final SqlParameterSource params = where == null ? null : where.getParams();
        final String orderClause = orderBy == null ? "\nORDER BY id" : "\n" + orderBy.getOrderClause();
        final String limitStr = limit == null ? "" : "\nLIMIT " + limit;
        final String query = FIND_ALL_QUERY.formatted(tableName, whereClause, orderClause, limitStr);
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
        return findOne(query, params, mapper);
    }

    protected <U> Optional<U> findOne(final String query, final SqlParameterSource params, RowMapper<U> mapper) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(query, params, mapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(final String query, final SqlParameterSource params) {
        return new ArrayList<>(findMany(query, params, mapper));
    }

    protected <U> Set<U> findMany(final String query, final SqlParameterSource params, RowMapper<U> mapper) {
        final Set<U> entities = new LinkedHashSet<>();
        jdbc.query(query, params, rs -> {
            entities.add(mapper.mapRow(rs, 0));
        });
        return entities;
    }

    protected Where where(final String fieldName, final Operand operand, final Object value) {
        return new Where(fieldName, operand, value);
    }

    protected <S> Where where(final ManyToManyRelation<S> relation, final String fieldName, final Operand operand,
            final Object value) {
        return new Where(relation, fieldName, operand, value);
    }

    protected OrderBy orderBy(final String fieldName) {
        return new OrderBy(fieldName);
    }

    protected OrderBy orderBy(final String fieldName, final Order order) {
        return new OrderBy(fieldName, order);
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

    protected class ManyToOneRelation<S> {

        private static final String MANAGE_RELATION_QUERY = """
                UPDATE %s
                SET %s = :relatedId
                WHERE id = :id;
                """;
        private static final String FETCH_RELATION_QUERY = """
                SELECT r.*
                FROM %s AS r
                JOIN %s AS b ON r.id = b.%s
                WHERE b.id = :id;
                """;
        private static final String FETCH_RELATIONS_QUERY = """
                SELECT b.id AS base__id,
                    r.*
                FROM %s AS r
                JOIN %s AS b ON r.id = b.%s
                WHERE b.id IN (%s);
                """;

        private final RowMapper<S> mapper;
        private final String joinedTable;
        private final String joinedColumn;

        public ManyToOneRelation(final Class<S> relatedModel) {
            final String baseName = relatedModel.getSimpleName().toLowerCase();
            this.joinedTable = baseName + "s";
            this.joinedColumn = baseName + "_id";
            this.mapper = new BeanPropertyRowMapper<>(relatedModel);
        }

        public void addRelation(final long id, final long relatedId) {
            final String query = MANAGE_RELATION_QUERY.formatted(tableName, joinedColumn);
            final SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("relatedId", relatedId);
            execute(query, params);
        }

        public void dropRelation(final long id) {
            final String query = MANAGE_RELATION_QUERY.formatted(tableName, joinedColumn);
            final SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("relatedId", null);
            execute(query, params);
        }

        public Optional<S> fetchRelation(final long id) {
            final String query = FETCH_RELATION_QUERY.formatted(joinedTable, tableName, joinedColumn);
            final SqlParameterSource params = new MapSqlParameterSource("id", id);
            return findOne(query, params, mapper);
        }

        public Map<Long, S> fetchRelations(final Set<Long> ids) {
            final String idsStr = ids.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            final String query = FETCH_RELATIONS_QUERY.formatted(joinedTable, tableName, joinedColumn, idsStr);
            final Map<Long, S> relationById = new HashMap<>();
            jdbc.getJdbcOperations().query(query, rs -> {
                Long id = rs.getLong("base__id");
                S relation = mapper.mapRow(rs, 0);
                relationById.put(id, relation);
            });
            return relationById;
        }
    }

    protected class ManyToManyRelation<S> {

        private static final String MANAGE_RELATION_QUERY = """
                MERGE INTO %s (%s, %s)
                KEY (%s, %s)
                VALUES (:id, :relatedId);
                """;
        private static final String MANAGE_RELATION_WITH_PAYLOAD_QUERY = """
                MERGE INTO %s (%s, %s, %s)
                KEY (%s, %s)
                VALUES (:id, :relatedId, :payload);
                """;
        private static final String FETCH_RELATION_QUERY = """
                SELECT r.*
                FROM %s AS r
                JOIN %s AS j ON r.id = j.%s
                WHERE j.%s = :id
                ORDER BY r.id;
                """;
        private static final String FETCH_RELATIONS_QUERY = """
                SELECT j.%s,
                    r.*
                FROM %s AS r
                JOIN %s AS j ON r.id = j.%s
                WHERE j.%s IN (%s)
                ORDER BY r.id;
                """;
        private static final String DROP_ALL_RELATIONS_QUERY = """
                DELETE FROM %s
                WHERE %s = :id;
                """;
        private static final String DROP_RELATIONS_EXCEPT_QUERY = """
                DELETE FROM %s
                WHERE %s = :id AND %s NOT IN (%S);
                """;
        private static final String DROP_RELATION_QUERY = """
                DELETE FROM %s
                WHERE %s = :id AND %s = :relatedId;
                """;
        private static final String DROP_RELATION_WITH_PAYLOAD_QUERY = """
                DELETE FROM %s
                WHERE %s = :id AND %s = :relatedId AND %s = :payload;
                """;

        private final RowMapper<S> mapper;

        @Getter
        private final String joinedTable;

        @Getter
        private final String baseColumn;

        @Getter
        private String joinedColumn;

        @Getter
        private String joinTable;

        private String payloadColumn;

        public ManyToManyRelation(final Class<S> relatedModel) {
            final String baseName = relatedModel.getSimpleName().toLowerCase();
            this.joinedTable = baseName + "s";
            this.joinedColumn = baseName + "_id";
            this.joinTable = BaseDbStorage.this.baseName + "_" + joinedTable;
            this.baseColumn = BaseDbStorage.this.baseName + "_id";
            this.payloadColumn = "status_id";
            this.mapper = new BeanPropertyRowMapper<>(relatedModel);
        }

        public ManyToManyRelation<S> withJoinTable(final String joinTable) {
            this.joinTable = joinTable;
            return this;
        }

        public ManyToManyRelation<S> withJoinedColumn(final String joinedColumn) {
            this.joinedColumn = joinedColumn;
            return this;
        }

        public ManyToManyRelation<S> withPayloadColumn(final String payloadColumn) {
            this.payloadColumn = payloadColumn;
            return this;
        }

        public void addRelation(final long id, final long relatedId) {
            addRelations(id, Set.of(relatedId));
        }

        public void addRelation(final long id, final long relatedId, final Object payload) {
            final String query = MANAGE_RELATION_WITH_PAYLOAD_QUERY.formatted(joinTable, baseColumn, joinedColumn,
                    payloadColumn, baseColumn, joinedColumn);
            final SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("relatedId", relatedId)
                    .addValue("payload", payload);
            execute(query, params);
        }

        public void saveRelations(final long id, final Set<Long> relatedIds) {
            addRelations(id, relatedIds);
            dropRelationsExcept(id, relatedIds);
        }

        public Set<S> fetchRelations(final long id) {
            final String query = FETCH_RELATION_QUERY.formatted(joinedTable, joinTable, joinedColumn, baseColumn);
            final SqlParameterSource params = new MapSqlParameterSource("id", id);
            return findMany(query, params, mapper);
        }

        public Map<Long, Set<S>> fetchRelations(final Set<Long> ids) {
            final String idsStr = ids.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            final String query = FETCH_RELATIONS_QUERY.formatted(baseColumn, joinedTable, joinTable, joinedColumn,
                    baseColumn, idsStr);
            final Map<Long, Set<S>> relationsById = new HashMap<>();
            jdbc.getJdbcOperations().query(query, rs -> {
                Long id = rs.getLong(baseColumn);
                S relation = mapper.mapRow(rs, 0);
                relationsById.computeIfAbsent(id, key -> new LinkedHashSet<>()).add(relation);
            });
            return relationsById;
        }

        public void dropRelations(final long id) {
            final String query = DROP_ALL_RELATIONS_QUERY.formatted(joinTable, baseColumn);
            final SqlParameterSource params = new MapSqlParameterSource("id", id);
            execute(query, params);
        }

        public boolean dropRelation(final long id, final long relatedId) {
            final String query = DROP_RELATION_QUERY.formatted(joinTable, baseColumn, joinedColumn);
            final SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("relatedId", relatedId);
            return execute(query, params) > 0;
        }

        public void dropRelation(final long id, final long relatedId, final Object payload) {
            final String query = DROP_RELATION_WITH_PAYLOAD_QUERY.formatted(joinTable, baseColumn, joinedColumn,
                    payloadColumn);
            final SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("id", id)
                    .addValue("relatedId", relatedId)
                    .addValue("payload", payload);
            execute(query, params);
        }

        private void addRelations(final long id, final Set<Long> relatedIds) {
            if (CollectionUtils.isEmpty(relatedIds)) {
                return;
            }
            final String query = MANAGE_RELATION_QUERY.formatted(joinTable, baseColumn, joinedColumn, baseColumn,
                    joinedColumn);
            final SqlParameterSource[] params = relatedIds.stream()
                    .map(relatedId -> new MapSqlParameterSource()
                            .addValue("id", id)
                            .addValue("relatedId", relatedId)
                    )
                    .toArray(SqlParameterSource[]::new);
            jdbc.batchUpdate(query, params);
        }

        private void dropRelationsExcept(final long id, final Set<Long> relatedIds) {
            if (CollectionUtils.isEmpty(relatedIds)) {
                dropRelations(id);
                return;
            }
            final String relatedIdsStr = relatedIds.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            final String query = DROP_RELATIONS_EXCEPT_QUERY.formatted(joinTable, baseColumn, joinedColumn,
                    relatedIdsStr);
            final SqlParameterSource params = new MapSqlParameterSource("id", id);
            execute(query, params);
        }
    }

    protected class Where {

        private final StringBuilder condition = new StringBuilder();
        private final MapSqlParameterSource params = new MapSqlParameterSource();
        private long lastParamNumber;

        public Where(final String fieldName, final Operand operand, final Object value) {
            this.condition.append(toCondition(fieldName, operand, appendParam(value)));
        }

        public <S> Where(final ManyToManyRelation<S> relation, final String fieldName, final Operand operand,
                final Object value) {
            this.condition.append(toCondition(relation, fieldName, operand, appendParam(value)));
        }

        public Where and(final String fieldName, final Operand operand, final Object value) {
            appendCondition("AND", toCondition(fieldName, operand, appendParam(value)));
            return this;
        }

        public <S> Where and(final ManyToManyRelation<S> relation, final String fieldName, final Operand operand,
                final Object value) {
            appendCondition("AND", toCondition(relation, fieldName, operand, appendParam(value)));
            return this;
        }

        public Where or(final String fieldName, final Operand operand, final Object value) {
            appendCondition("OR", toCondition(fieldName, operand, appendParam(value)));
            return this;
        }

        public <S> Where or(final ManyToManyRelation<S> relation, final String fieldName, final Operand operand,
                final Object value) {
            appendCondition("OR", toCondition(relation, fieldName, operand, appendParam(value)));
            return this;
        }

        public String getCondition() {
            return "WHERE " + condition;
        }

        public SqlParameterSource getParams() {
            return params;
        }

        private String appendParam(final Object value) {
            final String paramName = "param" + lastParamNumber++;
            params.addValue(paramName, value);
            return paramName;
        }

        private void appendCondition(final String joiner, final String conditionToAppend) {
            condition
                    .insert(0, '(')
                    .append(' ')
                    .append(joiner)
                    .append(' ')
                    .append(conditionToAppend)
                    .append(')');
        }

        private String toCondition(final String fieldName, final Operand operand, final String paramName) {
            return toDbNotation(fieldName) + " " + operand.getDbNotation() + " :" + paramName;
        }

        private <S> String toCondition(final ManyToManyRelation<S> relation, final String fieldName,
                final Operand operand, final String paramName) {
            return """
                        id IN (
                          SELECT j.%s
                          FROM %s AS j JOIN %s AS r ON j.%s = r.id
                          WHERE r.%s %s :%s
                        )
                        """.formatted(relation.getBaseColumn(), relation.getJoinTable(), relation.getJoinedTable(),
                    relation.getJoinedColumn(), fieldName, operand.getDbNotation(), paramName);
        }
    }

    protected class OrderBy {

        private final StringBuilder orderClause = new StringBuilder();

        public OrderBy(final String fieldName) {
            orderClause.append(toDbNotation(fieldName));
        }

        public OrderBy(final String fieldName, final Order order) {
            orderClause.append(toDbNotation(fieldName)).append(' ').append(order);
        }

        public OrderBy andThenBy(final String fieldName) {
            orderClause.append(", ").append(toDbNotation(fieldName));
            return this;
        }

        public OrderBy andThenBy(final String fieldName, final Order order) {
            orderClause.append(", ").append(toDbNotation(fieldName)).append(' ').append(order);
            return this;
        }

        public String getOrderClause() {
            return "ORDER BY " + orderClause;
        }
    }

    protected enum Operand {
        EQ("="),
        LIKE("ILIKE");

        private final String dbNotation;

        Operand(final String dbNotation) {
            this.dbNotation = dbNotation;
        }

        String getDbNotation() {
            return this.dbNotation;
        }
    }

    protected enum Order {
        ASC,
        DESC
    }
}
