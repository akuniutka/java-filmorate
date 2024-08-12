package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmGenre;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.api.GenreService;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreMapper {

    private final GenreService genreService;

    public Genre mapToGenre(final FilmGenre dto) {
        return genreService.getGenre(dto.getId()).orElseThrow(
                () -> new ValidationException("Check that genre id is correct (you sent %s)".formatted(dto.getId()))
        );
    }

    public Collection<Genre> mapToGenre(final Collection<FilmGenre> dtos) {
        return dtos.stream()
                .map(this::mapToGenre)
                .collect(Collectors.toSet());
    }

    public GenreDto mapToDto(final Genre genre) {
        final GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public Collection<GenreDto> mapToDto(final Collection<Genre> genres) {
        return genres.stream()
                .map(this::mapToDto)
                .toList();
    }
}
