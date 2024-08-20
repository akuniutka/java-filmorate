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

    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews ORDER BY review_date;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = :id;";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = :id;";
    private static final String DELETE_LIKES_DISLIKES_QUERY = "DELETE FROM film_likes_dislikes WHERE review_id = :id;";


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
    public Optional<Review> findById(final long id) {
        return findById(FIND_BY_ID_QUERY, id);
    }

    @Override
    public void delete(final long id) {
        delete(DELETE_LIKES_DISLIKES_QUERY, id);
        delete(DELETE_QUERY, id);
    }
}
