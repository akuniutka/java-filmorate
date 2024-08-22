package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface EventStorage {

    Collection<Event> findAll(Long userId);

    void save(Event event);

    void update(Event event);

    void delete(long id);


}
