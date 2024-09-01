package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.api.ReviewStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {

    private static final String ADD_LIKE_QUERY = """
            MERGE INTO review_likes (review_id, user_id, is_like)
            KEY (review_id, user_id)
            VALUES (:reviewId, :userId, :isLike);
            """;
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM review_likes
            WHERE review_id = :reviewId AND user_id = :userId AND is_like = :isLike;
            """;

    @Autowired
    public ReviewDbStorage(final NamedParameterJdbcTemplate jdbc) {
        super(Review.class, jdbc);
    }

    @Override
    public Review save(final Review review) {
        return save(List.of("content", "isPositive", "userId", "filmId"), review);
    }

    @Override
    public Collection<Review> findAll() {
        return findAll(
                desc("useful").asc("id")
        );
    }

    @Override
    public Collection<Review> findAllByFilmId(final long filmId, final long count) {
        return findAll(
                and().eq("filmId", filmId),
                desc("useful").asc("id"),
                count
        );
    }

    @Override
    public Optional<Review> update(final Review review) {
        return update(List.of("content", "isPositive"), review);
    }

    @Override
    public Review addLike(final long reviewId, final long userId) {
        return addLike(reviewId, userId, true);
    }

    @Override
    public Review addDislike(final long reviewId, final long userId) {
        return addLike(reviewId, userId, false);
    }

    @Override
    public Review deleteLike(long reviewId, long userId) {
        return deleteLike(reviewId, userId, true);
    }

    @Override
    public Review deleteDislike(long reviewId, long userId) {
        return deleteLike(reviewId, userId, false);
    }

    private Review addLike(final long reviewId, final long userId, final boolean isLike) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("userId", userId)
                .addValue("isLike", isLike);
        execute(ADD_LIKE_QUERY, params);
        return findById(reviewId).orElseThrow();
    }

    private Review deleteLike(final long reviewId, final long userId, final boolean isLike) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reviewId", reviewId)
                .addValue("userId", userId)
                .addValue("isLike", isLike);
        execute(DELETE_LIKE_QUERY, params);
        return findById(reviewId).orElseThrow();
    }
}
