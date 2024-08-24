package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.api.DirectorStorage;

@Component
@Slf4j
public class DirectorInMemoryStorage extends BaseInMemoryStorage<Director> implements DirectorStorage {

    public DirectorInMemoryStorage() {
        super(Director::getId, Director::setId);
    }
}
