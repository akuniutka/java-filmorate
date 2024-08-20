package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"reviewId"})
public class ReviewDto {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Integer useful;
    private Long filmId;
    private Long userId;
    private Instant reviewDate;
}
