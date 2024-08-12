package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.storage.AbstractUserStorageTest;

class UserInMemoryStorageTest extends AbstractUserStorageTest {

    UserInMemoryStorageTest() {
        this.userStorage = new UserInMemoryStorage();
    }
}