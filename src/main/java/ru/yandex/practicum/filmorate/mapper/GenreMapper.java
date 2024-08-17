package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.FilmGenre;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.api.GenreService;

import java.util.Collection;
import java.util.stream.Collectors;

@Mapper
public abstract class GenreMapper {

    protected GenreService genreService;

    @Autowired
    public void setGenreService(GenreService genreService) {
        this.genreService = genreService;
    }

    public abstract GenreDto mapToDto(Genre genre);

    public abstract Collection<GenreDto> mapToDto(Collection<Genre> genres);

    public Genre mapToGenre(final FilmGenre dto) {
        return dto == null ? null : genreService.getGenre(dto.getId()).orElseThrow(
                () -> new ValidationException("Check that genre id is correct (you sent %s)".formatted(dto.getId()))
        );
    }

    public Collection<Genre> mapToGenre(final Collection<FilmGenre> dtos) {
        return dtos == null ? null : dtos.stream()
                .map(this::mapToGenre)
                .collect(Collectors.toSet());
    }
}
