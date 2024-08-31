package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.Collection;

public interface EventService {

    void create(EventType eventType, long userId, Operation operation, Long entityId);

    Collection<Event> getEvents(Long userId);
}
