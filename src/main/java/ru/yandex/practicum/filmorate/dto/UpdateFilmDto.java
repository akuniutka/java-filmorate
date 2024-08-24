package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.IsAfter;

import java.time.LocalDate;
import java.util.Collection;

@Data
@EqualsAndHashCode(of = {"id"})
public class UpdateFilmDto {

    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @IsAfter("1895-12-27")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NotNull
    @Positive
    private Integer duration;

    private FilmMpa mpa;
    private Collection<FilmGenre> genres;
    private Collection<FilmDirector> directors;
}
