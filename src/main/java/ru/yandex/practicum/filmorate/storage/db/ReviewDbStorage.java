package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.ReviewStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDbStorage extends BaseDbStorage<Review> implements ReviewStorage {

    private final ManyToManyRelation<User> likes;

    @Autowired
    public ReviewDbStorage(final NamedParameterJdbcTemplate jdbc) {
        super(Review.class, jdbc);
        this.likes = new ManyToManyRelation<>(User.class)
                .withJoinTable("review_likes")
                .withPayloadColumn("is_like");
    }

    @Override
    public Review save(final Review review) {
        return save(List.of("content", "isPositive", "userId", "filmId"), review);
    }

    @Override
    public Collection<Review> findAllOrderByUsefulDesc(final long count) {
        return find(
                null,
                desc("useful").asc("id"),
                count
        );
    }

    @Override
    public Collection<Review> findByFilmIdOrderByUsefulDesc(final long filmId, final long count) {
        return find(
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
        likes.addRelation(id, userId, true);
        return findById(id).orElseThrow();
    }

    @Override
    public Review addDislike(final long id, final long userId) {
        likes.addRelation(id, userId, false);
        return findById(id).orElseThrow();
    }

    @Override
    public Review deleteLike(final long id, final long userId) {
        likes.dropRelation(id, userId, true);
        return findById(id).orElseThrow();
    }

    @Override
    public Review deleteDislike(final long id, final long userId) {
        likes.dropRelation(id, userId, false);
        return findById(id).orElseThrow();
    }
}
