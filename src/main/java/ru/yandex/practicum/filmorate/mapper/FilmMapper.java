package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class FilmMapper {

    private final MpaMapper mpaMapper;
    private final GenreMapper genreMapper;

    public Film mapToFilm(final NewFilmDto dto) {
        final Film film = new Film();
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        if (dto.getMpa() != null) {
            film.setMpa(mpaMapper.mapToMpa(dto.getMpa()));
        }
        if (dto.getGenres() != null) {
            film.setGenres(genreMapper.mapToGenre(dto.getGenres()));
        }
        return film;
    }

    public Film mapToFilm(final UpdateFilmDto dto) {
        final Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());
        if (dto.getMpa() != null) {
            film.setMpa(mpaMapper.mapToMpa(dto.getMpa()));
        }
        if (dto.getGenres() != null) {
            film.setGenres(genreMapper.mapToGenre(dto.getGenres()));
        }
        return film;
    }

    public FilmDto mapToDto(final Film film) {
        final FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        if (film.getMpa() != null) {
            dto.setMpa(mpaMapper.mapToDto(film.getMpa()));
        }
        if (film.getGenres() != null) {
            dto.setGenres(genreMapper.mapToDto(film.getGenres()));
        }
        return dto;
    }

    public Collection<FilmDto> mapToDto(final Collection<Film> films) {
        return films.stream()
                .map(this::mapToDto)
                .toList();
    }
}
