package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Optional;

@Repository
public class EventDbStorage extends BaseDbStorage<Event> implements EventStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM events WHERE user_id = :user_id ORDER BY time_stamp";

    private static final String SAVE_QUERY = """
            SELECT * FROM FINAL TABLE (
              INSERT INTO events (user_id, event_type, operation, entity_id)
              VALUES (:userId, :eventType, :operation, :entityId)
            );
            """;

    private static final String UPDATE_QUERY = """
            SELECT * FROM FINAL TABLE (
              UPDATE events
              SET user_id = :userId,
                event_type = :eventType,
                operation = :operation,
                entity_id = :entityId,
                time_stamp = :timestamp
              WHERE event_id = :id
            );
            """;

    @Autowired
    public EventDbStorage(final NamedParameterJdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(Event.class, jdbc, mapper);

    }

    @Override
    public Collection<Event> findAll(long userId) {
        var params = new MapSqlParameterSource()
                .addValue("user_id", userId);
        return findMany(FIND_ALL_QUERY, params);
    }

    @Override
    public Event save(final Event event) {
        var params = new MapSqlParameterSource()
                .addValue("userId", event.getUserId())
                .addValue("eventType", event.getEventType().name())
                .addValue("operation", event.getOperation().name())
                .addValue("entityId", event.getEntityId());
        return findOne(SAVE_QUERY, params).orElseThrow();
    }

    @Override
    public Optional<Event> update(final Event event) {
        var params = new MapSqlParameterSource()
                .addValue("id", event.getId())
                .addValue("userId", event.getUserId())
                .addValue("eventType", event.getEventType().name())
                .addValue("operation", event.getOperation().name())
                .addValue("entityId", event.getEntityId())
                .addValue("timestamp", Timestamp.from(event.getTimestamp()));
        return findOne(UPDATE_QUERY, params);
    }
}
