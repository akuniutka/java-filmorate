package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.api.EventService;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.service.api.ReviewService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.ReviewStorage;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final FilmService filmService;
    private final UserService userService;
    private final ReviewStorage reviewStorage;
    private final EventService eventService;

    @Override
    public Collection<Review> getReviews() {
        return reviewStorage.findAll();
    }

    @Override
    public Collection<Review> getReviewsForFilm(final long filmId, final long count) {
        return reviewStorage.findAllByFilmId(filmId, count);
    }

    @Override
    public Review getReview(final long id) {
        return reviewStorage.findById(id).orElseThrow(
                () -> new NotFoundException(Review.class, id)
        );
    }

    @Override
    public void deleteReview(final long id) {
        long userId = getReview(id).getUserId();
        if (reviewStorage.delete(id)) {
            eventService.createEvent(EventType.REVIEW, userId, Operation.REMOVE, id);
        }
    }

    @Override
    public Review createReview(final Review review) {
        Objects.requireNonNull(review, "Cannot create Review: is null");
        filmService.getFilm(review.getFilmId());
        userService.getUser(review.getUserId());
        final Review reviewStored = reviewStorage.save(review);
        log.info("Created new Review: {}", reviewStored);
        // добавление события добавления отзыва в таблицу events
        eventService.createEvent(EventType.REVIEW, review.getUserId(), Operation.ADD, reviewStored.getId());
        return reviewStored;
    }

    @Override
    public Review updateReview(final Review review) {
        Objects.requireNonNull(review, "Cannot update review: is null");
        final Review reviewStored = reviewStorage.update(review).orElseThrow(
                () -> new NotFoundException(Review.class, review.getId())
        );
        // добавление события обновления отзыва в таблицу events
        eventService.createEvent(EventType.REVIEW, reviewStored.getUserId(), Operation.UPDATE, reviewStored.getId());
        log.info("Updated review: {}", reviewStored);
        return reviewStored;
    }

    @Override
    public Review addLike(final long reviewId, final long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        return reviewStorage.addLike(reviewId, userId);
    }

    @Override
    public Review deleteLike(final long reviewId, final long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        return reviewStorage.deleteLike(reviewId, userId);
    }

    @Override
    public Review addDislike(final long reviewId, final long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        return reviewStorage.addDislike(reviewId, userId);
    }

    @Override
    public Review deleteDislike(final long reviewId, final long userId) {
        getReview(reviewId);
        userService.getUser(userId);
        return reviewStorage.deleteDislike(reviewId, userId);
    }
}
