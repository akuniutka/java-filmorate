package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.api.ReviewStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews ORDER BY useful DESC;";
    private static final String FIND_ALL_REVIEW_FOR_FILM = "SELECT * FROM reviews WHERE film_id = :film_id ORDER BY useful DESC LIMIT :count;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = :id;";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = :id;";
    private static final String DELETE_LIKES_DISLIKES_QUERY = "DELETE FROM film_likes_dislikes WHERE review_id = :id;";


    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM film_likes_dislikes
            WHERE review_id = :review_id AND user_id = :user_id;
            """;
    private static final String SAVE_QUERY = """
            SELECT * FROM FINAL TABLE (
              INSERT INTO reviews ( content, is_positive, user_id, film_id, useful, review_date)
              VALUES (:content, :is_positive, :user_id, :film_id, :useful, :review_date)
            );
            """;

    private static final String UPDATE_QUERY = """
            SELECT * FROM FINAL TABLE (
              UPDATE reviews
              SET content = :content,
                is_positive = :is_positive,
                user_id = :user_id,
                film_id = :film_id,
                useful = :useful
              WHERE review_id = :review_id
            );
            """;
    private static final String ADD_LIKE_QUERY = """
            INSERT INTO film_likes_dislikes (user_id, review_id, is_like, create_datetime)
            VALUES (:user_id, :review_id, :is_like, :create_datetime);
            """;
    private static final String UPDATE_QUERY_INCREMENT_USEFUL = """
            UPDATE reviews
              SET useful = (SELECT useful FROM reviews WHERE review_id = :review_id) + 1
              WHERE review_id = :review_id;
            """;

    private static final String UPDATE_QUERY_DECREMENT_USEFUL = """
            UPDATE reviews
              SET useful = (SELECT useful FROM reviews WHERE review_id = :review_id) - 1
              WHERE review_id = :review_id;
            """;
    @Autowired
    public ReviewDbStorage(final NamedParameterJdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);

    }

    @Override
    public Review save(final Review review) {
        var params = new MapSqlParameterSource()
                .addValue("review_id", review.getId())
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("user_id", review.getUserId())
                .addValue("film_id", review.getFilmId())
                .addValue("useful", review.getUseful())
                .addValue("review_date", review.getReviewDate());
        return findOne(SAVE_QUERY, params).orElseThrow();
    }

    @Override
    public Optional<Review> update(final Review review) {
        var params = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("user_id", review.getUserId())
                .addValue("film_id", review.getFilmId())
                .addValue("useful", review.getUseful())
                .addValue("review_id", review.getId());
        return findOne(UPDATE_QUERY, params);
    }

    @Override
    public Collection<Review> findAll() {
        return findAll(FIND_ALL_QUERY);
    }

    @Override
    public Collection<Review> getReviewsForFilm(long filmId, long count) {
        var params = new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("count", count);
        return findMany(FIND_ALL_REVIEW_FOR_FILM, params);
    }

    @Override
    public Optional<Review> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void delete(final long id) {
        delete(DELETE_LIKES_DISLIKES_QUERY, id);
        delete(DELETE_QUERY, id);
    }

    @Override
    public Review addLike(long reviewId, long userId) {
        var params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("review_id", reviewId)
                .addValue("is_like", true)
                .addValue("create_datetime", Instant.now());
        execute(ADD_LIKE_QUERY, params);
        params = new MapSqlParameterSource()
                .addValue("review_id", reviewId);
        execute(UPDATE_QUERY_INCREMENT_USEFUL, params);
        return findById(reviewId).get();
    }

    @Override
    public Review addDislike(long reviewId, long userId) {
        var params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("review_id", reviewId)
                .addValue("is_like", false)
                .addValue("create_datetime", Instant.now());
        execute(ADD_LIKE_QUERY, params);
        params = new MapSqlParameterSource()
                .addValue("review_id", reviewId);
        execute(UPDATE_QUERY_DECREMENT_USEFUL, params);
        return findById(reviewId).get();
    }

    @Override
    public Review deleteLike(long reviewId, long userId) {
        var params = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);
        execute(DELETE_LIKE_QUERY, params);
        params = new MapSqlParameterSource()
                .addValue("review_id", reviewId);
        execute(UPDATE_QUERY_DECREMENT_USEFUL, params);
        return findById(reviewId).get();
    }

    @Override
    public Review deleteDislike(long reviewId, long userId) {
        var params = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId);
        execute(DELETE_LIKE_QUERY, params);
        params = new MapSqlParameterSource()
                .addValue("review_id", reviewId);
        execute(UPDATE_QUERY_INCREMENT_USEFUL, params);
        return findById(reviewId).get();
    }
}
