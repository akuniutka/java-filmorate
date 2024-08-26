package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.api.MpaStorage;

@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {

    @Autowired
    public MpaDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<Mpa> mapper) {
        super(Mpa.class, jdbc, mapper);
    }
}
