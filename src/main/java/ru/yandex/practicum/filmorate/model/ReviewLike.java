package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"userId"})
public class ReviewLike {
    private Long userId;
    private Long rewiewId;
    private boolean isLike;
}
