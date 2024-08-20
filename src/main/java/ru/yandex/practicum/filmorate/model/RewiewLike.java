package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"id"})
public class RewiewLike {
        private Long rewiewId;
        private Long userId;
        private Instant likeCreateDate;
        private boolean isLike;
    }
