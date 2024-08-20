package ru.yandex.practicum.filmorate.storage.db.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rewiew;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RewiewRowMapper implements RowMapper<Rewiew> {
    @Override
    public Rewiew mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rewiew rewiew = new Rewiew();
        rewiew.setId(rs.getLong("rewiew_id"));
        rewiew.setContent(rs.getString("content"));
        rewiew.setPositive(rs.getBoolean("is_positive"));
        rewiew.setUserId(rs.getLong("user_id"));
        rewiew.setFilmId(rs.getLong("film_id"));
        rewiew.setUseful(rs.getInt("useful"));
        rewiew.setRewiewDate(rs.getTimestamp("rewiew_date").toInstant());
        return rewiew;
    }
}
