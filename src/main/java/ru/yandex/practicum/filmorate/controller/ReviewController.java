package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.api.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper mapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ReviewDto> getReviews(@RequestParam(name = "filmId", required = false) Long filmId, @RequestParam(name = "count", defaultValue = "10", required = false) Integer count) {
        log.info("Received GET at /reviews");
        if (filmId == null)
            return mapper.mapToDto(reviewService.getReviews());
        Collection<ReviewDto> dtos = mapper.mapToDto(reviewService.getReviewsForFilm(filmId, count));
        log.info("Responded to GET /reviews: {}", dtos);
        return dtos;
    }


    @PostMapping
    public ReviewDto createReview(@Valid @RequestBody final NewReviewDto newReviewDto) {
        log.info("Received POST at /reviews: {}", newReviewDto);
        final Review review = mapper.mapToReview(newReviewDto);
        final ReviewDto reviewDto = mapper.mapToDto(reviewService.createReview(review));
        log.info("Responded to POST /reviews: {}", reviewDto);
        return reviewDto;
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody final UpdateReviewDto updateReviewDto) {
        log.info("Received PUT at /reviews");
        final Review review = mapper.mapToReview(updateReviewDto);
        final ReviewDto reviewDto = reviewService.updateReview(review).map(mapper::mapToDto).orElseThrow(
                () -> new NotFoundException(Review.class, updateReviewDto.getReviewId())
        );
        log.info("Responded to PUT at /reviews: {}", reviewDto);
        return reviewDto;
    }

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable final long id) {
        log.info("Received GET at /reviews/{}", id);
        final ReviewDto dto = reviewService.getReview(id).map(mapper::mapToDto).orElseThrow(
                () -> new NotFoundException(Review.class, id)
        );
        log.info("Responded to GET /reviews/{}: {}", id, dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable final long id) {
        log.info("Received DELETE at /reviews/{}", id);
        reviewService.deleteReview(id);
        log.info("Responded to DELETE /reviews/{}", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addLike(@PathVariable final long id, @PathVariable final long userId) {
        log.info("Received PUT at /reviews/{}/like/{}", id, userId);
        final ReviewDto reviewDto = mapper.mapToDto(reviewService.addLike(id, userId));
        log.info("Responded to PUT /reviews/{}/like/{}", id, userId);
        return reviewDto;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto deleteLike(@PathVariable final long id, @PathVariable final long userId) {
        log.info("Received DELETE at /reviews/{}/like/{}", id, userId);
        final ReviewDto reviewDto = mapper.mapToDto(reviewService.deleteLike(id, userId));
        log.info("Responded to DELETE /reviews/{}/like/{}", id, userId);
        return reviewDto;
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addDislike(@PathVariable final long id, @PathVariable final long userId) {
        log.info("Received PUT at /reviews/{}/dislike/{}", id, userId);
        final ReviewDto reviewDto = mapper.mapToDto(reviewService.addDislike(id, userId));
        log.info("Responded to PUT /reviews/{}/dislike/{}", id, userId);
        return reviewDto;
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto deleteDislike(@PathVariable final long id, @PathVariable final long userId) {
        log.info("Received DELETE at /reviews/{}/dislike/{}", id, userId);
        final ReviewDto reviewDto = mapper.mapToDto(reviewService.deleteDislike(id, userId));
        log.info("Responded to DELETE /reviews/{}/dislike/{}", id, userId);
        return reviewDto;
    }
}