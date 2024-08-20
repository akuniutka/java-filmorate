package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewService {
    Collection<Review> getReviews();

    Optional<Review> getReview(long id);

     Review createReview(Review review);

    Optional<Review> updateReview(Review review);

    void deleteReview(long id);

//    void addLikeToReview(long id, long friendId);
//
//    void addDislikeToReview(long id, long userId);
//
//    void deleteLikeToReview(long id, long userId);
//
//    void deleteDislikeToReview(long id, long userId);
}

