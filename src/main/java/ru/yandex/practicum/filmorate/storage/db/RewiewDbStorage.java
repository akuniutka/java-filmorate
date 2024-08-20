package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rewiew;
import ru.yandex.practicum.filmorate.storage.api.RewiewStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class RewiewDbStorage extends BaseDbStorage<Rewiew>  implements RewiewStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM rewiews ORDER BY rewiew_date;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rewiews WHERE rewiew_id = :id;";


    @Autowired
    public RewiewDbStorage(final NamedParameterJdbcTemplate jdbc, RowMapper<Rewiew> mapper) {
        super(jdbc, mapper);

    }
    @Override
    public Collection<Rewiew> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    @Override
    public Optional<Rewiew> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }
}
