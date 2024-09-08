package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.FilmDirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Mapper
public interface DirectorMapper {

    Director mapToDirector(NewDirectorDto dto);

    Director mapToDirector(UpdateDirectorDto dto);

    Collection<Director> mapToDirector(Collection<FilmDirectorDto> dtos);

    DirectorDto mapToDto(Director director);

    Collection<DirectorDto> mapToDto(Collection<Director> directors);
}
