package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"userId", "reviewId"})
public class ReviewLike {
    private Long userId;
    private Long reviewId;
    private boolean isLike;
}
