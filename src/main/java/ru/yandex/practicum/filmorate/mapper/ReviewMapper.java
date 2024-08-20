package ru.yandex.practicum.filmorate.mapper;


import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.time.Instant;
import java.util.Collection;

@Mapper
public abstract class ReviewMapper {

    public Review mapToReview(NewReviewDto dto) {
        Review review = new Review();
        review.setId(dto.getReviewId());
        review.setContent(dto.getContent());
        review.setPositive(dto.isPositive());
        review.setUseful(0);
        review.setFilmId(dto.getFilmId());
        review.setUserId(dto.getUserId());
        review.setReviewDate(Instant.now());
        return review;
    }
//
public Review mapToReview(UpdateReviewDto dto) {
    Review review = new Review();
    review.setId(dto.getReviewId());
    review.setContent(dto.getContent());
    review.setPositive(dto.isPositive());
    review.setUseful(dto.getUseful());
    review.setFilmId(dto.getFilmId());
    review.setUserId(dto.getUserId());
    return review;
}
    public ReviewDto mapToDto(Review review)
    {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getId());
        dto.setContent(review.getContent());
        dto.setPositive(review.isPositive());
        dto.setUseful(review.getUseful());
        dto.setFilmId(review.getFilmId());
        dto.setUserId(review.getUserId());
        return dto;
    }

    public abstract Collection<ReviewDto> mapToDto(Collection<Review> reviews);
}
