package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.api.ReviewStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {

    private static final String SAVE_QUERY = """
            SELECT *
            FROM FINAL TABLE (
              INSERT INTO reviews (content, is_positive, user_id, film_id)
              VALUES (:content, :isPositive, :userId, :filmId)
            );
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT r.id,
              r.content,
              r.is_positive,
              r.user_id,
              r.film_id,
              rr.rating AS useful
            FROM reviews AS r
            JOIN review_ratings AS rr on r.id = rr.review_id
            WHERE r.id = :id;
            """;
    private static final String UPDATE_QUERY = """
            SELECT r.id,
              r.content,
              r.is_positive,
              r.user_id,
              r.film_id,
              rr.rating AS useful
            FROM FINAL TABLE (
              UPDATE reviews
              SET content = :content,
                is_positive = :isPositive
              WHERE id = :id
            ) AS r
            JOIN review_ratings AS rr on r.id = rr.review_id;
            """;
    private static final String FIND_ALL_QUERY = """
            SELECT r.id,
              r.content,
              r.is_positive,
              r.user_id,
              r.film_id,
              rr.rating AS useful
            FROM reviews AS r
            JOIN review_ratings AS rr on r.id = rr.review_id
            ORDER BY useful DESC, r.id;
            """;
    private static final String FIND_ALL_REVIEW_FOR_FILM = """
            SELECT r.id,
              r.content,
              r.is_positive,
              r.user_id,
              r.film_id,
              rr.rating AS useful
            FROM reviews AS r
            JOIN review_ratings AS rr on r.id = rr.review_id
            WHERE film_id = :film_id
            ORDER BY useful DESC, r.id
            LIMIT :count;
            """;
    private static final String ADD_LIKE_QUERY = """
            MERGE INTO reviews_likes (review_id, user_id, is_like)
            KEY (review_id, user_id)
            VALUES (:reviewId, :userId, :isLike);
            """;
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM reviews_likes
            WHERE review_id = :reviewId AND user_id = :userId AND is_like = :isLike;
            """;

    @Autowired
    public ReviewDbStorage(final NamedParameterJdbcTemplate jdbc, final RowMapper<Review> mapper) {
        super(Review.class, jdbc, mapper);
    }

    @Override
    public Review save(final Review review) {
        return save(SAVE_QUERY, review);
    }

    @Override
    public Optional<Review> findById(final long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_BY_ID_QUERY, Map.of("id", id), mapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Review> update(final Review review) {
        return update(UPDATE_QUERY, review);
    }

    @Override
    public Collection<Review> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    @Override
    public Collection<Review> getReviewsForFilm(final long filmId, final long count) {
        var params = new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("count", count);
        return findMany(FIND_ALL_REVIEW_FOR_FILM, params);
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
