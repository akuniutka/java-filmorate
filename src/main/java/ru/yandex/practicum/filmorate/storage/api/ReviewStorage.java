package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Collection<Review> findAll();

    Collection<Review> getReviewsForFilm(long filmId, long count);

    Optional<Review> findById(long id);

    Review save(Review review);

    Optional<Review> update(Review review);

    boolean delete(long id);

    Review addLike(long reviewId, long userId);

    Review addDislike(long reviewId, long userId);

    Review deleteLike(long id, long userId);

    Review deleteDislike(long id, long userId);
}
