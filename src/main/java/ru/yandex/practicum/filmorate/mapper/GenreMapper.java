package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmGenre;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Mapper
public interface GenreMapper {

    Genre mapToGenre(FilmGenre dto);

    Collection<Genre> mapToGenre(Collection<FilmGenre> dtos);

    GenreDto mapToDto(Genre genre);

    Collection<GenreDto> mapToDto(Collection<Genre> genres);
}
