package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class DirectorDto {

    private Long id;
    private String name;
}
