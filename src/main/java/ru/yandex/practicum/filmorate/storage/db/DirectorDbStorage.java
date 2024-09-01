package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.api.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDbStorage extends BaseDbStorage<Director> implements DirectorStorage {

    public DirectorDbStorage(final NamedParameterJdbcTemplate jdbc) {
        super(Director.class, jdbc);
    }

    @Override
    public Director save(final Director director) {
        return save(List.of("name"), director);
    }

    @Override
    public Optional<Director> update(final Director director) {
        return update(List.of("name"), director);
    }
}
