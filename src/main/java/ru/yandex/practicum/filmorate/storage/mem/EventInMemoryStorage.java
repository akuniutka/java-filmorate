package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;

import java.util.Collection;
import java.util.Collections;

public class EventInMemoryStorage extends BaseInMemoryStorage<Event> implements EventStorage {

    public EventInMemoryStorage() {
        super(Event::getId, Event::setId);
    }

    @Override
    public Collection<Event> findAll(long userId) {
        return Collections.emptyList();
    }
}
