package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.api.ReviewService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.EventStorage;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.storage.api.ReviewStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;


    @Override
    public Collection<Review> getReviews() {
        return reviewStorage.findAll();
    }

    @Override
    public Collection<Review> getReviewsForFilm(long filmId, long count) {
        return reviewStorage.getReviewsForFilm(filmId, count);
    }

    @Override
    public Optional<Review> getReview(final long id) {
        return reviewStorage.findById(id);
    }

    @Override
    public void deleteReview(long id) {
        assertReviewExist(id);
        long userId = reviewStorage.findById(id).get().getUserId();
        reviewStorage.delete(id);

        Event event = new Event();
        event.setEventType(EventType.REVIEW);
        event.setUserId(userId);
        event.setOperation(Operation.REMOVE);
        event.setTimestamp(Instant.now());
        event.setEntityId(id);
        eventStorage.save(event);
    }


    @Override
    public Review createReview(final Review review) {
        assertFilmExist(review.getFilmId());
        assertUserExist(review.getUserId());
        Objects.requireNonNull(review, "Cannot create Review: is null");
        final Review reviewStored = reviewStorage.save(review);
        log.info("Created new Review: {}", reviewStored);
        // добавление события добавления отзыва в таблицу events
        Event event = new Event();
        event.setEventType(EventType.REVIEW);
        event.setUserId(review.getUserId());
        event.setOperation(Operation.ADD);
        event.setTimestamp(Instant.now());
        event.setEntityId(reviewStored.getId());
        eventStorage.save(event);
        return reviewStored;
    }

    @Override
    public Optional<Review> updateReview(final Review review) {
        Objects.requireNonNull(review, "Cannot update Review: is null");
        final Optional<Review> reviewStored = reviewStorage.update(review);
        reviewStored.ifPresent(u -> log.info("Updated Review: {}", u));
        // добавление события добавления отзыва в таблицу events
        Event event = new Event();
        event.setEventType(EventType.REVIEW);
        event.setUserId(review.getUserId());
        event.setOperation(Operation.UPDATE);
        event.setTimestamp(Instant.now());
        event.setEntityId(review.getId());
        eventStorage.save(event);
        return reviewStored;
    }


    @Override
    public Review addLike(final long reviewId, final long userId) {
        assertReviewExist(reviewId);
        assertUserExist(userId);

        // добавление события добавления лайка в таблицу events
        Event event = new Event();
        event.setEventType(EventType.LIKE);
        event.setUserId(userId);
        event.setOperation(Operation.ADD);
        event.setTimestamp(Instant.now());
        event.setEntityId(reviewId);
        eventStorage.save(event);
        return reviewStorage.addLike(reviewId, userId);
    }

    @Override
    public Review deleteLike(final long reviewId, final long userId) {
        assertReviewExist(reviewId);
        assertUserExist(userId);
        // добавление события удаления лайка в таблицу events
        Event event = new Event();
        event.setEventType(EventType.LIKE);
        event.setUserId(userId);
        event.setOperation(Operation.REMOVE);
        event.setTimestamp(Instant.now());
        event.setEntityId(reviewId);
        eventStorage.save(event);
        return reviewStorage.deleteLike(reviewId, userId);
    }

    @Override
    public Review addDislike(final long reviewId, final long userId) {
        assertReviewExist(reviewId);
        assertUserExist(userId);
        // добавление события добавления лайка в таблицу events
        Event event = new Event();
        event.setEventType(EventType.LIKE);
        event.setUserId(userId);
        event.setOperation(Operation.ADD);
        event.setTimestamp(Instant.now());
        event.setEntityId(reviewId);
        eventStorage.save(event);
        return reviewStorage.addDislike(reviewId, userId);
    }

    @Override
    public Review deleteDislike(final long reviewId, final long userId) {
        assertReviewExist(reviewId);
        assertUserExist(userId);
        // добавление события удаления лайка в таблицу events
        Event event = new Event();
        event.setEventType(EventType.LIKE);
        event.setUserId(userId);
        event.setOperation(Operation.REMOVE);
        event.setTimestamp(Instant.now());
        event.setEntityId(reviewId);
        eventStorage.save(event);
        return reviewStorage.deleteDislike(reviewId, userId);
    }

    private void assertReviewExist(final long id) {
        reviewStorage.findById(id).orElseThrow(() -> new NotFoundException(Review.class, id));
    }

    private void assertFilmExist(final long id) {
        filmStorage.findById(id).orElseThrow(() -> new NotFoundException(Film.class, id));
    }

    private void assertUserExist(final long id) {
        userService.getUser(id).orElseThrow(() -> new NotFoundException(User.class, id));
    }
}
