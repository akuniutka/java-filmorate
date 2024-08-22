package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;

import java.util.Collection;

public class EventInMemoryStorage implements EventStorage {
    @Override
    public Collection<Event> findAll(Long userId) {
        return null;
    }

    @Override
    public void save(Event event) {

    }

    @Override
    public void update(Event event) {

    }

    @Override
    public void delete(long id) {

    }
}
