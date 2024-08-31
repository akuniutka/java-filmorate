package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"reviewId"})
public class ReviewDto {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Long filmId;
    private Long userId;
    private Long useful;
}
