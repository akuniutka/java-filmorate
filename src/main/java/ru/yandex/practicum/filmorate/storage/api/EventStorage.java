package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventStorage {

    Event save(Event event);

    Collection<Event> findAll();

    Collection<Event> findAllByUserId(long userId);

    void deleteAll();
}
