package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"id"})
public class RewiewDto {
    private Long id;
    private String content;
    private boolean isPositive;
    private Integer useful;
    private Long filmId;
    private Long userId;
    private Instant rewiewDate;
}
