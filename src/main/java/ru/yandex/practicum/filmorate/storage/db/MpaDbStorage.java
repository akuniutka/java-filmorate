package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.api.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa ORDER BY mpa_id;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE mpa_id = :id;";

    @Autowired
    public MpaDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Mpa> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    public Optional<Mpa> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }
}
