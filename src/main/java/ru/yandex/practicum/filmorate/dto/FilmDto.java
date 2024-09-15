package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;

@Data
@EqualsAndHashCode(of = {"id"})
public class FilmDto {

    private Long id;
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    private Integer duration;
    private MpaDto mpa;
    private Collection<GenreDto> genres;
    private Collection<DirectorDto> directors;
    private BigDecimal rating;
}
