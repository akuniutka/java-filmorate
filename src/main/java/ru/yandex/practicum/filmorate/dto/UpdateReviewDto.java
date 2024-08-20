package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@EqualsAndHashCode(of = {"reviewId"})
public class UpdateReviewDto {
    private Long reviewId;
    @NotBlank
    private String content;
    @NotNull
    private boolean isPositive;
    private Integer useful;
    @NotNull
    private Long filmId;
    @NotNull
    private Long userId;
}

