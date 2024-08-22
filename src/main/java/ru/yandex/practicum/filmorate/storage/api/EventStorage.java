package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventStorage {

    Collection<Event> findAll(Long userId);

    void save(Event event);

    void update(Event event);

    void delete(long id);


}
