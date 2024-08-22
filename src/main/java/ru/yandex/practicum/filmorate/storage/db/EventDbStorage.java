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
              INSERT INTO events ( user_id, event_type, operation, entity_id, time_stamp)
              VALUES (:user_id, :event_type, :operation, :entity_id, :time_stamp)
            );
            """;

    private static final String UPDATE_QUERY = """
            SELECT * FROM FINAL TABLE (
              UPDATE events
              SET user_id = :user_id,
                event_type = :event_type,
                operation = :operation,
                entity_id = :entity_id,
                time_stamp = :time_stamp
              WHERE event_id = :event_id
            );
            """;

    private static final String DELETE_QUERY = """
            DELETE FROM events
            WHERE event_id = :event_id;
            """;
    @Autowired
    public EventDbStorage(final NamedParameterJdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);

    }
    @Override
    public Collection<Event> findAll(Long userId) {
        var params = new MapSqlParameterSource()
                .addValue("user_id", userId);
        return findMany(FIND_ALL_QUERY, params);
    }

    @Override
    public void save(Event event) {
        var params = new MapSqlParameterSource()
                .addValue("event_id", event.getId())
                .addValue("user_id", event.getUserId())
                .addValue("event_type", event.getEventType().name())
                .addValue("operation", event.getOperation().name())
                .addValue("entity_id", event.getEntityId())
                .addValue("time_stamp", Timestamp.from(event.getTimestamp()));
        execute(SAVE_QUERY, params);
    }

    @Override
    public void update(Event event) {
        var params = new MapSqlParameterSource()
                .addValue("event_id", event.getId())
                .addValue("user_id", event.getUserId())
                .addValue("event_type", event.getEventType().name())
                .addValue("operation", event.getOperation().name())
                .addValue("entity_id", event.getEntityId())
                .addValue("time_stamp", Timestamp.from(event.getTimestamp()));
        execute(UPDATE_QUERY, params);
    }

    @Override
    public void delete(long id) {
        delete(DELETE_QUERY, id);
    }
}
