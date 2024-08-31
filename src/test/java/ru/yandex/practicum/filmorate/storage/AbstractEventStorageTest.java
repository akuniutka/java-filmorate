package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.assertEventListEquals;
import static ru.yandex.practicum.filmorate.TestModels.cloneEvent;
import static ru.yandex.practicum.filmorate.TestModels.faker;

public abstract class AbstractEventStorageTest {

    protected EventStorage eventStorage;
    protected long[] userIds = {1, 2, 1};

    @AfterEach
    void tearDown() {
        eventStorage.deleteAll();
    }

    @Test
    void shouldReturnCorrectEventsList() {
        final List<Event> expectedEvents = preloadData();

        final List<Event> actualEvents = new ArrayList<>(eventStorage.findAll());

        assertEventListEquals(expectedEvents, actualEvents);
    }

    @Test
    void shouldReturnCorrectEventsListByUserId() {
        final List<Event> expectedEvents = preloadData();
        final long id = userIds[0];
        expectedEvents.remove(1);

        final List<Event> actualEvents = new ArrayList<>(eventStorage.findAllByUserId(id));

        assertEventListEquals(expectedEvents, actualEvents);
    }

    @Test
    void shouldReturnEmptyEventListForUnknownUserIs() {
        preloadData();
        final long wrongUserId = -1L;

        final List<Event> actualEvents = new ArrayList<>(eventStorage.findAllByUserId(wrongUserId));

        assertTrue(actualEvents.isEmpty(), "should find no events for user with id = " + wrongUserId);
    }

    @Test
    void shouldDeleteAllEvents() {
        preloadData();

        eventStorage.deleteAll();
        final Collection<Event> actualEvents = eventStorage.findAll();

        assertTrue(actualEvents.isEmpty(), "should no event remain");
    }

    protected List<Event> preloadData() {
        final List<Event> expectedEvents = new ArrayList<>();
        final List<Event> savedEvents = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Event event = new Event();
            event.setUserId(userIds[i]);
            event.setEntityId(faker.number().randomNumber());
            event.setEventType(EventType.values()[i]);
            event.setOperation(Operation.values()[i]);
            Event savedEvent = eventStorage.save(cloneEvent(event));
            event.setId(savedEvent.getId());
            event.setTimestamp(savedEvent.getTimestamp());
            expectedEvents.add(event);
            savedEvents.add(savedEvent);
        }
        assertEventListEquals(expectedEvents, savedEvents);
        return expectedEvents;
    }
}
