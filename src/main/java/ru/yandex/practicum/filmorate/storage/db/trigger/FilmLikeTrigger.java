package ru.yandex.practicum.filmorate.storage.db.trigger;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FilmLikeTrigger implements Trigger {

    private static final String INCREASE_LIKES_QUERY = """
            UPDATE films
            SET likes = likes + 1
            WHERE id = ?;
            """;
    private static final String DECREASE_LIKES_QUERY = """
            UPDATE films
            SET likes = likes - 1
            WHERE id = ?;
            """;

    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        if (oldRow != null) {
            final long filmId = (Long) oldRow[0];
            updateFilmLikes(connection, DECREASE_LIKES_QUERY, filmId);
        }
        if (newRow != null) {
            final long filmId = (Long) newRow[0];
            updateFilmLikes(connection, INCREASE_LIKES_QUERY, filmId);
        }
    }

    private void updateFilmLikes(Connection connection, final String query, final long filmId) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, filmId);
        statement.executeUpdate();
    }
}
