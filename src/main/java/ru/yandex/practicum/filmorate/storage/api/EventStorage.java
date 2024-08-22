package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface EventStorage {

    Collection<Event> findAll(Long userId);

    Event save(Event event);

    Optional<Event> update(Event event);

    void delete(long id);


}
