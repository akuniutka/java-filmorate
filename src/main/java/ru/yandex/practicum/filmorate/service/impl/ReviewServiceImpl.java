package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.ReviewService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.storage.api.ReviewStorage;

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


    @Override
    public Collection<Review> getReviews() {
        return reviewStorage.findAll();
    }

    @Override
    public Optional<Review> getReview(final long id) {
        return reviewStorage.findById(id);
    }

    @Override
    public void deleteReview(long id) {
        assertReviewExist(id);
        reviewStorage.delete(id);
    }



    @Override
    public Review createReview(final Review review) {
        assertFilmExist(review.getFilmId());
        assertUserExist(review.getUserId());
        Objects.requireNonNull(review, "Cannot create Review: is null");
        final Review reviewStored = reviewStorage.save(review);
        log.info("Created new Review: {}", reviewStored);
        return reviewStored;
    }

    @Override
    public Optional<Review> updateReview(final Review review) {
        Objects.requireNonNull(review, "Cannot update Review: is null");
        final Optional<Review> reviewStored = reviewStorage.update(review);
        reviewStored.ifPresent(u -> log.info("Updated Review: {}", u));
        return reviewStored;
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
