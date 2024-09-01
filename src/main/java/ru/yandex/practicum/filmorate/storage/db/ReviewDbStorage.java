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
            VALUES (:id, :userId, :isLike);
            """;
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM review_likes
            WHERE review_id = :id AND user_id = :userId AND is_like = :isLike;
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
    public Collection<Review> findAllOrderByUsefulDesc(final long count) {
        return findAll(
                null,
                desc("useful").asc("id"),
                count
        );
    }

    @Override
    public Collection<Review> findAllByFilmIdOrderByUsefulDesc(final long filmId, final long count) {
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
    public Review addLike(final long id, final long userId) {
        return addLike(id, userId, true);
    }

    @Override
    public Review addDislike(final long id, final long userId) {
        return addLike(id, userId, false);
    }

    @Override
    public Review deleteLike(final long id, final long userId) {
        return deleteLike(id, userId, true);
    }

    @Override
    public Review deleteDislike(final long id, final long userId) {
        return deleteLike(id, userId, false);
    }

    private Review addLike(final long id, final long userId, final boolean isLike) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId)
                .addValue("isLike", isLike);
        execute(ADD_LIKE_QUERY, params);
        return findById(id).orElseThrow();
    }

    private Review deleteLike(final long id, final long userId, final boolean isLike) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId)
                .addValue("isLike", isLike);
        execute(DELETE_LIKE_QUERY, params);
        return findById(id).orElseThrow();
    }
}
