package ru.yandex.practicum.filmorate.storage.db.trigger;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReviewLikeTrigger implements Trigger {

    private static final String INCREASE_LIKES_QUERY = """
            UPDATE reviews
            SET useful = useful + 1
            WHERE id = ?;
            """;
    private static final String DECREASE_LIKES_QUERY = """
            UPDATE reviews
            SET useful = useful - 1
            WHERE id = ?;
            """;

    @Override
    public void fire(final Connection connection, final Object[] oldRow, final Object[] newRow) throws SQLException {
        if (oldRow != null) {
            final boolean isLike = (Boolean) oldRow[2];
            final long reviewId = (Long) oldRow[0];
            if (isLike) {
                updateReviewLikes(connection, DECREASE_LIKES_QUERY, reviewId);
            } else {
                updateReviewLikes(connection, INCREASE_LIKES_QUERY, reviewId);
            }
        }
        if (newRow != null) {
            final boolean isLike = (Boolean) newRow[2];
            final long reviewId = (Long) newRow[0];
            if (isLike) {
                updateReviewLikes(connection, INCREASE_LIKES_QUERY, reviewId);
            } else {
                updateReviewLikes(connection, DECREASE_LIKES_QUERY, reviewId);
            }
        }
    }

    private void updateReviewLikes(Connection connection, final String query, final long reviewId) throws SQLException {
        final PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, reviewId);
        statement.executeUpdate();
    }
}
