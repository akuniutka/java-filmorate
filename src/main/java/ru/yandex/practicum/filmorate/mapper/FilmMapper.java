package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Mapper(uses = {MpaMapper.class, GenreMapper.class, DirectorMapper.class})
public interface FilmMapper {

    Film mapToFilm(NewFilmDto dto);

    Film mapToFilm(UpdateFilmDto dto);

    FilmDto mapToDto(Film film);

    Collection<FilmDto> mapToDto(Collection<Film> films);
}
