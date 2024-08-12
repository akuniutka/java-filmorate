package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.storage.AbstractFilmStorageTest;

class FilmInMemoryStorageTest extends AbstractFilmStorageTest {

    FilmInMemoryStorageTest() {
        this.filmStorage = new FilmInMemoryStorage();
    }
}