package ru.yandex.practicum.filmorate.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.filmorate.dto.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Mapper
public abstract class ReviewMapper {


    public Review mapToReview(NewReviewDto dto) {
        Review review = new Review();
        review.setContent(dto.getContent());
        review.setIsPositive(dto.getIsPositive());
        review.setUseful(0L);
        review.setFilmId(dto.getFilmId());
        review.setUserId(dto.getUserId());
        return review;
    }

    @Mapping(target = "id", source = "reviewId")
    public abstract Review mapToReview(UpdateReviewDto dto);

    @Mapping(target = "reviewId", source = "id")
    public abstract ReviewDto mapToDto(Review review);

    public abstract Collection<ReviewDto> mapToDto(Collection<Review> reviews);
}

