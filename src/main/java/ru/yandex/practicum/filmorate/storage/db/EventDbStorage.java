package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;

import java.util.Collection;
import java.util.List;

@Repository
public class EventDbStorage extends BaseDbStorage<Event> implements EventStorage {

    @Autowired
    public EventDbStorage(final NamedParameterJdbcTemplate jdbc) {
        super(Event.class, jdbc);
    }

    @Override
    public Event save(final Event event) {
        return save(List.of("userId", "eventType", "operation", "entityId"), event);
    }

    @Override
    public Collection<Event> findByUserId(final long userId) {
        return find(
                where("userId", Operand.EQ, userId)
        );
    }
}
