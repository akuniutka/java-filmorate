package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmGenreDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Mapper
public interface GenreMapper {

    Genre mapToGenre(FilmGenreDto dto);

    Collection<Genre> mapToGenre(Collection<FilmGenreDto> dtos);

    GenreDto mapToDto(Genre genre);

    Collection<GenreDto> mapToDto(Collection<Genre> genres);
}
