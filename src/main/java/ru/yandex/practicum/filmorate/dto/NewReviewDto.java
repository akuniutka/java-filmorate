package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class NewReviewDto {
    private Long reviewId;
    @NotBlank
    private String content;
    @NotNull
    private boolean isPositive;
    @NotNull
    private Long filmId;
    @NotNull
    private Long userId;
}
