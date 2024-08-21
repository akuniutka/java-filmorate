package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.FilmDirector;
import ru.yandex.practicum.filmorate.dto.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.api.DirectorService;

import java.util.Collection;

@Mapper
public abstract class DirectorMapper {

    private DirectorService directorService;

    @Autowired
    public void setDirectorService(DirectorService directorService) {
        this.directorService = directorService;
    }

    public abstract Director mapToDirector(NewDirectorDto dto);

    public abstract Director mapToDirector(UpdateDirectorDto dto);

    public abstract DirectorDto mapToDto(Director director);

    public abstract Collection<DirectorDto> mapToDto(Collection<Director> directors);

    public Director mapToDirector(final FilmDirector dto) {
        return dto == null ? null : directorService.getDirector(dto.getId()).orElseThrow(
                () -> new ValidationException("Checl that director id is correct (you sent %s".formatted(dto.getId()))
        );
    }
}
