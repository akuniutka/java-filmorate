package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.api.ReviewService;

import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController extends BaseController {

    private final ReviewService reviewService;
    private final ReviewMapper mapper;

    @PostMapping
    public ReviewDto createReview(
            @Valid @RequestBody final NewReviewDto newReviewDto,
            final HttpServletRequest request
    ) {
        logRequest(request, newReviewDto);
        final Review review = mapper.mapToReview(newReviewDto);
        final ReviewDto reviewDto = mapper.mapToDto(reviewService.createReview(review));
        logResponse(request, reviewDto);
        return reviewDto;
    }

    @GetMapping("/{id}")
    public ReviewDto getReview(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ReviewDto dto = mapper.mapToDto(reviewService.getReview(id));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ReviewDto> getReviews(
            @RequestParam(name = "filmId", required = false) Long filmId,
            @RequestParam(name = "count", defaultValue = "10") long count,
            final HttpServletRequest request
    ) {
        logRequest(request);
        Collection<ReviewDto> dtos = mapper.mapToDto(reviewService.getReviews(filmId, count));
        logResponse(request, dtos);
        return dtos;
    }

    @PutMapping
    public ReviewDto updateReview(
            @Valid @RequestBody final UpdateReviewDto updateReviewDto,
            final HttpServletRequest request
    ) {
        logRequest(request, updateReviewDto);
        final Review review = mapper.mapToReview(updateReviewDto);
        final ReviewDto reviewDto = mapper.mapToDto(reviewService.updateReview(review));
        logResponse(request, reviewDto);
        return reviewDto;
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addLike(
            @PathVariable final long id,
            @PathVariable final long userId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ReviewDto dto = mapper.mapToDto(reviewService.addLike(id, userId));
        logResponse(request, dto);
        return dto;
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addDislike(
            @PathVariable final long id,
            @PathVariable final long userId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ReviewDto dto = mapper.mapToDto(reviewService.addDislike(id, userId));
        logResponse(request, dto);
        return dto;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto deleteLike(
            @PathVariable final long id,
            @PathVariable final long userId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ReviewDto dto = mapper.mapToDto(reviewService.deleteLike(id, userId));
        logResponse(request, dto);
        return dto;
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto deleteDislike(
            @PathVariable final long id,
            @PathVariable final long userId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ReviewDto dto = mapper.mapToDto(reviewService.deleteDislike(id, userId));
        logResponse(request, dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        reviewService.deleteReview(id);
        logResponse(request);
    }
}
