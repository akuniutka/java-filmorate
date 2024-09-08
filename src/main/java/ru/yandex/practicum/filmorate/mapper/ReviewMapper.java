package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.NewReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdateReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

@Mapper
public interface ReviewMapper {

    Review mapToReview(NewReviewDto dto);

    Review mapToReview(UpdateReviewDto dto);

    ReviewDto mapToDto(Review review);

    Collection<ReviewDto> mapToDto(Collection<Review> reviews);
}

