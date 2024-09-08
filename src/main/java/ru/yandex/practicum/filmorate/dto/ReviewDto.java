package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class ReviewDto {

    @JsonProperty("reviewId")
    private Long id;

    private String content;
    private Boolean isPositive;
    private Long filmId;
    private Long userId;
    private Long useful;
}
