package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.api.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews ORDER BY useful DESC;";
    private static final String FIND_ALL_REVIEW_FOR_FILM = "SELECT * FROM reviews WHERE film_id = :film_id ORDER BY useful DESC LIMIT :count;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = :id;";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = :id;";
    private static final String DELETE_LIKES_DISLIKES_QUERY = "DELETE FROM reviews_likes_dislikes WHERE review_id = :id;";

    private static final String SELECT_LIKE_TO_REVIEW = "SELECT COUNT(*) FROM reviews_likes_dislikes " +
            "WHERE review_id = :review_id AND user_id = :user_id AND is_like = :is_like;";

    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM reviews_likes_dislikes
            WHERE review_id = :review_id AND user_id = :user_id;
            """;
    private static final String SAVE_QUERY = """
            SELECT * FROM FINAL TABLE (
              INSERT INTO reviews ( content, is_positive, user_id, film_id, useful)
              VALUES (:content, :is_positive, :user_id, :film_id, :useful)
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
            INSERT INTO reviews_likes_dislikes (user_id, review_id, is_like)
            VALUES (:user_id, :review_id, :is_like);
            """;

    private static final String UPDATE_QUERY_USEFUL = """
            UPDATE reviews
              SET useful = :useful
              WHERE review_id = :review_id;
            """;


    private static final String UPDATE_LIKES = """
            UPDATE reviews_likes_dislikes
              SET is_like = :is_like
              WHERE review_id = :review_id AND user_id = :user_id;
            """;


    private static final String GET_REWIEW_USEFUL = """
            SELECT useful FROM reviews WHERE review_id = :review_id;
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
                .addValue("useful", review.getUseful());
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
        if (isLikeExist(reviewId, userId, false)) {


            long useful = getUsefulCount(reviewId) + 2;
            var params = new MapSqlParameterSource()
                    .addValue("review_id", reviewId)
                    .addValue("useful", useful);
            execute(UPDATE_QUERY_USEFUL, params);
            params = new MapSqlParameterSource()
                    .addValue("user_id", userId)
                    .addValue("review_id", reviewId)
                    .addValue("is_like", true);
            execute(UPDATE_LIKES, params);
            return findById(reviewId).get();
        }
        if (isLikeExist(reviewId, userId, true)) {
            return findById(reviewId).get();
        }
        var params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("review_id", reviewId)
                .addValue("is_like", true);
        execute(ADD_LIKE_QUERY, params);
        long useful = getUsefulCount(reviewId) + 1;
        params = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("useful", useful);
        execute(UPDATE_QUERY_USEFUL, params);
        return findById(reviewId).get();
    }

    @Override
    public Review addDislike(long reviewId, long userId) {

        if (isLikeExist(reviewId, userId, true)) {


            long useful = getUsefulCount(reviewId) - 2;
            var params = new MapSqlParameterSource()
                    .addValue("review_id", reviewId)
                    .addValue("useful", useful);
            execute(UPDATE_QUERY_USEFUL, params);

            params = new MapSqlParameterSource()
                    .addValue("user_id", userId)
                    .addValue("review_id", reviewId)
                    .addValue("is_like", false);
            execute(UPDATE_LIKES, params);
            return findById(reviewId).get();
        }
        if (isLikeExist(reviewId, userId, false)) {
            return findById(reviewId).get();
        }
        var params = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("review_id", reviewId)
                .addValue("is_like", false);
        execute(ADD_LIKE_QUERY, params);
        long useful = getUsefulCount(reviewId) - 1;
        params = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("useful", useful);
        execute(UPDATE_QUERY_USEFUL, params);
        return findById(reviewId).get();
    }

    @Override
    public Review deleteLike(long reviewId, long userId) {

        if (isLikeExist(reviewId, userId, true)) {
            var params = new MapSqlParameterSource()
                    .addValue("review_id", reviewId)
                    .addValue("user_id", userId);
            execute(DELETE_LIKE_QUERY, params);
            long useful = getUsefulCount(reviewId) - 1;

            params = new MapSqlParameterSource()
                    .addValue("review_id", reviewId)
                    .addValue("useful", useful);
            execute(UPDATE_QUERY_USEFUL, params);

        }
        return findById(reviewId).get();
    }

    @Override
    public Review deleteDislike(long reviewId, long userId) {
        if (isLikeExist(reviewId, userId, true)) {
            var params = new MapSqlParameterSource()
                    .addValue("review_id", reviewId)
                    .addValue("user_id", userId);
            execute(DELETE_LIKE_QUERY, params);
            long useful = getUsefulCount(reviewId) + 1;
            params = new MapSqlParameterSource()
                    .addValue("review_id", reviewId)
                    .addValue("useful", useful);
            execute(UPDATE_QUERY_USEFUL, params);

        }
        return findById(reviewId).get();
    }

    public Long getUsefulCount(long reviewId) {
        var params = new MapSqlParameterSource()
                .addValue("review_id", reviewId);
        return jdbc.queryForObject(GET_REWIEW_USEFUL, params, Long.class);
    }

    Boolean isLikeExist(long reviewId, long userId, boolean status) {
        var params = new MapSqlParameterSource()
                .addValue("review_id", reviewId)
                .addValue("user_id", userId)
                .addValue("is_like", status);
        Integer count = jdbc.queryForObject(SELECT_LIKE_TO_REVIEW, params, Integer.class);
        return count > 0;
    }
}
