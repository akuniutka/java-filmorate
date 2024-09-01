package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {

    Review createReview(Review review);

    Review getReview(long id);

    Collection<Review> getReviews(Long filmId, long count);

    Review updateReview(Review review);

    Review addLike(long reviewId, long userId);

    Review addDislike(long reviewId, long userId);

    Review deleteLike(long reviewId, long userId);

    Review deleteDislike(long reviewId, long userId);

    void deleteReview(long id);
}

