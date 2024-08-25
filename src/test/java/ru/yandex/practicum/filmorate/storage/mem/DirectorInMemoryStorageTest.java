package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.storage.AbstractDirectorStorageTest;

public class DirectorInMemoryStorageTest extends AbstractDirectorStorageTest {

    DirectorInMemoryStorageTest() {
        this.directorStorage = new DirectorInMemoryStorage();
    }
}
