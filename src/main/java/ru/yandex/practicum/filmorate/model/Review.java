package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class Review {
    private Long id;
    private String content;
    private Boolean isPositive;
    private Integer useful;
    private Long filmId;
    private Long userId;
}