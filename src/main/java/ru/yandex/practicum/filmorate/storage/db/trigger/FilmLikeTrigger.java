package ru.yandex.practicum.filmorate.storage.db.trigger;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FilmLikeTrigger implements Trigger {

    private static final String INCREASE_LIKES_QUERY = """
            UPDATE films
            SET likes = likes + 1, marks_sum = marks_sum + ?
            WHERE id = ?;
            """;
    private static final String DECREASE_LIKES_QUERY = """
            UPDATE films
            SET likes = likes - 1, marks_sum = marks_sum - ?
            WHERE id = ?;
            """;

    @Override
    public void fire(
            final Connection connection,
            final Object[] oldRow, final Object[] newRow
    ) throws SQLException {
        if (oldRow != null) {
            final long filmId = (Long) oldRow[0];
            final int mark = (Integer) oldRow[2];
            updateFilmLikes(connection, DECREASE_LIKES_QUERY, filmId, mark);
        }
        if (newRow != null) {
            final long filmId = (Long) newRow[0];
            final int mark = (Integer) newRow[2];
            updateFilmLikes(connection, INCREASE_LIKES_QUERY, filmId, mark);
        }
    }

    private void updateFilmLikes(
            final Connection connection,
            final String query,
            final long filmId,
            final int mark
    ) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, mark);
        statement.setLong(2, filmId);
        statement.executeUpdate();
    }
}
