package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"id"})
public class ReviewLike {
        private Long userId;
        private Long rewiewId;
        private boolean isLike;
        private Instant createDatetime;
}
