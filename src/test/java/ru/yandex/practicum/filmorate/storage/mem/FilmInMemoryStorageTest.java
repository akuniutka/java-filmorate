package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.storage.AbstractFilmStorageTest;

class FilmInMemoryStorageTest extends AbstractFilmStorageTest {

    FilmInMemoryStorageTest() {
        this.userStorage = new UserInMemoryStorage();
        this.filmStorage = new FilmInMemoryStorage();
    }
}