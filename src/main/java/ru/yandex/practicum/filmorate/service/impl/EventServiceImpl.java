package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.api.EventService;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventStorage eventStorage;

    @Override
    public void createEvent(final EventType type, final long userId, final Operation operation, final long entityId) {
        final Event event = new Event();
        event.setEventType(type);
        event.setUserId(userId);
        event.setOperation(operation);
        event.setEntityId(entityId);
        final Event storedEvent = eventStorage.save(event);
        log.info("New event added to action log: {}", storedEvent);
    }

    @Override
    public Collection<Event> getEventsByUserId(final long userId) {
        return eventStorage.findByUserId(userId);
    }
}
