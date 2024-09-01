package ru.yandex.practicum.filmorate.controller;

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
public class FilmController {

    private final FilmService filmService;
    private final FilmMapper mapper;

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody final NewFilmDto newFilmDto) {
        log.info("Received POST at /films: {}", newFilmDto);
        final Film film = mapper.mapToFilm(newFilmDto);
        final FilmDto filmDto = mapper.mapToDto(filmService.createFilm(film));
        log.info("Responded to POST /films: {}", filmDto);
        return filmDto;
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable final long id) {
        log.info("Received GET at /films/{}", id);
        final FilmDto dto = mapper.mapToDto(filmService.getFilm(id));
        log.info("Responded to GET /films/{}: {}", id, dto);
        return dto;
    }

    @GetMapping
    public Collection<FilmDto> getFilms() {
        log.info("Received GET at /films");
        final Collection<FilmDto> dtos = mapper.mapToDto(filmService.getFilms());
        log.info("Responded to GET /films: {}", dtos);
        return dtos;
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getTopLiked(
            @RequestParam(defaultValue = "10") @Valid @Positive final long count,
            @RequestParam(required = false) final Long genreId,
            @RequestParam(required = false) final Integer year) {
        log.info("Received GET at /films/popular?count={}&genreId={}&year{}", count, genreId, year);
        final Collection<FilmDto> dtos = mapper.mapToDto(filmService.getTopFilms(count, genreId, year));
        log.info("Responded to GET /films/popular?count={}&genreId={}&year={}: {}", count, genreId, year, dtos);
        return dtos;
    }

    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> getFilmsByDirector(
            @PathVariable final long directorId,
            @RequestParam final String sortBy) {
        log.info("Received GET at /films/director/{}?sortBy={}", directorId, sortBy);
        final Collection<FilmDto> dtos =  mapper.mapToDto(filmService.getFilmsByDirectorId(directorId, sortBy));
        log.info("Responded to GET /films/director/{}?sortBy={}: {}", directorId, sortBy, dtos);
        return dtos;
    }

    @GetMapping("/search")
    public Collection<FilmDto> searchFilms(
            @RequestParam final String query,
            @RequestParam(required = false, defaultValue = "title") final String by) {
        log.info("Received GET at /films/search?query={}&by={}", query, by);
        final Collection<FilmDto> dtos = mapper.mapToDto(filmService.getFilmsByTitleAndDirectorName(query, by));
        log.info("Responded to GET /films/search?query={}&by={}: {}", query, by, dtos);
        return dtos;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable final long id, @PathVariable final long userId) {
        log.info("Received PUT at /films/{}/like/{}", id, userId);
        filmService.addLike(id, userId);
        log.info("Responded to PUT /films/{}/like/{} with no body", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable final long id, @PathVariable final long userId) {
        log.info("Received DELETE at /films/{}/like/{}", id, userId);
        filmService.deleteLike(id, userId);
        log.info("Responded to DELETE /films/{}/like/{} with no body", id, userId);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody final UpdateFilmDto updateFilmDto) {
        log.info("Received PUT at /films: {}", updateFilmDto);
        final Film film = mapper.mapToFilm(updateFilmDto);
        final FilmDto filmDto = mapper.mapToDto(filmService.updateFilm(film));
        log.info("Responded to PUT /films: {}", filmDto);
        return filmDto;
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable long filmId) {
        log.info("Received DELETE at /films/{}", filmId);
        filmService.deleteFilm(filmId);
        log.info("Responded to DELETE /films/{} with no body", filmId);
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(@RequestParam final long userId, @RequestParam final long friendId) {
        log.info("Received GET at /films/common?userId={}&friendId={}", userId, friendId);
        final Collection<FilmDto> dtos = mapper.mapToDto(filmService.getCommonFilms(userId, friendId));
        log.info("Responded to GET /films/common?userId={}&friendId={}: {}", userId, friendId, dtos);
        return dtos;
    }
}
