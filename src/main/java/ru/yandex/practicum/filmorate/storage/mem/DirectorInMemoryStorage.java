package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.api.DirectorStorage;

public class DirectorInMemoryStorage extends BaseInMemoryStorage<Director> implements DirectorStorage {

    public DirectorInMemoryStorage() {
        super(Director::getId, Director::setId);
    }
}
