package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.storage.AbstractEventStorageTest;

public class EventInMemoryStorageTest extends AbstractEventStorageTest {

    EventInMemoryStorageTest() {
        this.eventStorage = new EventInMemoryStorage();
    }
}
