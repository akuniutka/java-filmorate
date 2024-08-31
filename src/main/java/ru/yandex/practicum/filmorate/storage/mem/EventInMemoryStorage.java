package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

@Component
@Slf4j
public class EventInMemoryStorage extends BaseInMemoryStorage<Event> implements EventStorage {

    public EventInMemoryStorage() {
        super(Event::getId, Event::setId);
    }

    @Override
    public Event save(final Event event) {
        event.setTimestamp(Instant.now());
        return super.save(event);
    }

    @Override
    public Collection<Event> findAllByUserId(long userId) {
        return findAll().stream()
                .filter(event -> Objects.equals(event.getUserId(), userId))
                .toList();
    }
}
