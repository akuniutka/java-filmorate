package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class UpdateReviewDto {

    @JsonProperty("reviewId")
    private Long id;

    @NotBlank
    private String content;

    @NotNull
    private Boolean isPositive;
}

