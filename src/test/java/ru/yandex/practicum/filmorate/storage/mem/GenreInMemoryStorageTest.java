package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.storage.AbstractGenreStorageTest;

class GenreInMemoryStorageTest extends AbstractGenreStorageTest {

    GenreInMemoryStorageTest() {
        this.genreStorage = new GenreInMemoryStorage();
    }
}