package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {
    Collection<Review> getReviews();

    Collection<Review> getReviewsForFilm(long filmId, long count);

    Review getReview(long id);

    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(long id);

    Review addLike(long reviewId, long userId);

    Review addDislike(long reviewId, long userId);

    Review deleteLike(long reviewId, long userId);

    Review deleteDislike(long reviewId, long userId);
}

