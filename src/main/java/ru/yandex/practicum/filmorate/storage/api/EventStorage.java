package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;
import java.util.Optional;

public interface EventStorage {

    Collection<Event> findAll(long userId);

    Event save(Event event);

    Optional<Event> update(Event event);

    boolean delete(long id);
}
