package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.api.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController extends BaseController {

    private final FilmService filmService;
    private final FilmMapper mapper;

    @PostMapping
    public FilmDto createFilm(
            @Valid @RequestBody final NewFilmDto newFilmDto,
            final HttpServletRequest request
    ) {
        logRequest(request, newFilmDto);
        final Film film = mapper.mapToFilm(newFilmDto);
        final FilmDto filmDto = mapper.mapToDto(filmService.createFilm(film));
        logResponse(request, filmDto);
        return filmDto;
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final FilmDto dto = mapper.mapToDto(filmService.getFilm(id));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    public Collection<FilmDto> getFilms(
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Collection<FilmDto> dtos = mapper.mapToDto(filmService.getFilms());
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getTopLiked(
            @RequestParam(defaultValue = "10") @Valid @Positive final long count,
            @RequestParam(required = false) final Long genreId,
            @RequestParam(required = false) final Integer year,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Collection<FilmDto> dtos = mapper.mapToDto(filmService.getTopFilms(count, genreId, year));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> getFilmsByDirector(
            @PathVariable final long directorId,
            @RequestParam final String sortBy,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Collection<FilmDto> dtos =  mapper.mapToDto(filmService.getFilmsByDirectorId(directorId, sortBy));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/search")
    public Collection<FilmDto> searchFilms(
            @RequestParam final String query,
            @RequestParam(defaultValue = "title") final String by,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Collection<FilmDto> dtos = mapper.mapToDto(filmService.getFilmsByTitleAndDirectorName(query, by));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(
            @RequestParam final long userId,
            @RequestParam final long friendId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Collection<FilmDto> dtos = mapper.mapToDto(filmService.getCommonFilms(userId, friendId));
        logResponse(request, dtos);
        return dtos;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable final long id,
            @PathVariable final long userId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        filmService.addLike(id, userId);
        logResponse(request);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(
            @PathVariable final long id,
            @PathVariable final long userId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        filmService.deleteLike(id, userId);
        logResponse(request);
    }

    @PutMapping
    public FilmDto updateFilm(
            @Valid @RequestBody final UpdateFilmDto updateFilmDto,
            final HttpServletRequest request
    ) {
        logRequest(request, updateFilmDto);
        final Film film = mapper.mapToFilm(updateFilmDto);
        final FilmDto filmDto = mapper.mapToDto(filmService.updateFilm(film));
        logResponse(request, filmDto);
        return filmDto;
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(
            @PathVariable long filmId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        filmService.deleteFilm(filmId);
        logResponse(request);
    }
}
