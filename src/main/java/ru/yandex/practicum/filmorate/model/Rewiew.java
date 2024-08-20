package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class Rewiew {
        private Long id;
        private String content;
        private boolean isPositive;
        private Integer useful;
        private Film film;
        private User user;
    }