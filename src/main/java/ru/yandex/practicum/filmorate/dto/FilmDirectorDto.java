package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FilmDirectorDto {

    @NotNull
    private Long id;
}
