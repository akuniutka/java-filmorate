package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Collection<ReviewDto> getReviews() {
        log.info("Received GET at /reviews");
        final Collection<ReviewDto> dtos = mapper.mapToDto(reviewService.getReviews());
        log.info("Responded to GET /reviews: {}", dtos);
        return dtos;
    }

    @PostMapping
    public ReviewDto createReview(@Valid @RequestBody final NewReviewDto newReviewDto) {
        log.info("Received POST at /reviews: {}", newReviewDto);
        final Review review = mapper.mapToReview(newReviewDto);
        final ReviewDto reviewDto = mapper.mapToDto(reviewService.createReview(review));
        log.info("Responded to POST /films: {}", reviewDto);
        return reviewDto;
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody final UpdateReviewDto updateReviewDto) {
        log.info("Received PUT at /Reviews");
        final Review review = mapper.mapToReview(updateReviewDto);
        final ReviewDto reviewDto = reviewService.updateReview(review).map(mapper::mapToDto).orElseThrow(
                () -> new NotFoundException(Review.class, updateReviewDto.getReviewId())
        );
        log.info("Responded to PUT at /Reviews: {}", reviewDto);
        return reviewDto;
    }

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable final long id) {
        log.info("Received GET at /Reviews/{}", id);
        final ReviewDto dto = reviewService.getReview(id).map(mapper::mapToDto).orElseThrow(
                () -> new NotFoundException(Review.class, id)
        );
        log.info("Responded to GET /Reviews/{}: {}", id, dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable final long id) {
        log.info("Received DELETE at /Reviews/{}", id);
        reviewService.deleteReview(id);
        log.info("Responded to DELETE /Reviews/{} with no body", id);
    }
}
