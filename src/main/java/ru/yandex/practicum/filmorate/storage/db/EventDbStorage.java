package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;

import java.util.Collection;

@Repository
public class EventDbStorage extends BaseDbStorage<Event> implements EventStorage {

    private static final String SAVE_QUERY = """
            SELECT *
            FROM FINAL TABLE (
              INSERT INTO events (user_id, event_type, operation, entity_id)
              VALUES (:userId, :eventType, :operation, :entityId)
            );
            """;
    private static final String FIND_ALL_BY_USER_ID_QUERY = """
            SELECT *
            FROM events
            WHERE user_id = :userId
            ORDER BY id;
            """;

    @Autowired
    public EventDbStorage(final NamedParameterJdbcTemplate jdbc) {
        super(Event.class, jdbc);
    }

    @Override
    public Collection<Event> findAllByUserId(final long userId) {
        var params = new MapSqlParameterSource()
                .addValue("userId", userId);
        return findMany(FIND_ALL_BY_USER_ID_QUERY, params);
    }

    @Override
    public Event save(final Event event) {
        return save(SAVE_QUERY, event);
    }
}
