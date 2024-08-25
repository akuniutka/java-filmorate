package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.api.EventService;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;

import java.time.Instant;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventStorage eventStorage;

    @Override
    public void create(EventType eventType, long userId, Operation operation, Long entityId) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setUserId(userId);
        event.setOperation(operation);
        event.setTimestamp(Instant.now());
        event.setEntityId(entityId);
        eventStorage.save(event);
    }

    @Override
    public Collection<Event> getEvents(Long userId) {
        return eventStorage.findAll(userId);
    }
}
