package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.storage.AbstractMpaStorageTest;

class MpaInMemoryStorageTest extends AbstractMpaStorageTest {

    MpaInMemoryStorageTest() {
        this.mpaStorage = new MpaInMemoryStorage();
    }
}