package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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

    @GetMapping("/popular")
    public Collection<FilmDto> getTopLiked(@RequestParam(defaultValue = "10", required = false) @Valid @Positive final long count,
                                           @RequestParam(defaultValue = "0", required = false) @Valid  final Long genreId,
                                           @RequestParam(defaultValue = "0", required = false) @Valid final Integer year) {
        log.info("Received GET at /films/popular (count = {}, genreId = {}, year = {})", count, genreId,year);
        final Collection<FilmDto> dtos = mapper.mapToDto(filmService.getTopFilmsByLikes(count, genreId, year));
        log.info("Responded to GET /films/popular (count = {},genreId = {}, year = {} ): {}", count, genreId, year, dtos);
        return dtos;
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable final long id) {
        log.info("Received GET at /films/{}", id);
        final FilmDto dto = filmService.getFilm(id).map(mapper::mapToDto).orElseThrow(
                () -> new NotFoundException(Film.class, id)
        );
        log.info("Responded to GET /films/{}: {}", id, dto);
        return dto;
    }

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody final NewFilmDto newFilmDto) {
        log.info("Received POST at /films: {}", newFilmDto);
        final Film film = mapper.mapToFilm(newFilmDto);
        final FilmDto filmDto = mapper.mapToDto(filmService.createFilm(film));
        log.info("Responded to POST /films: {}", filmDto);
        return filmDto;
    }

    @GetMapping
    public Collection<FilmDto> getFilms() {
        log.info("Received GET at /films");
        final Collection<FilmDto> dtos = mapper.mapToDto(filmService.getFilms());
        log.info("Responded to GET /films: {}", dtos);
        return dtos;
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody final UpdateFilmDto updateFilmDto) {
        log.info("Received PUT at /films: {}", updateFilmDto);
        final Film film = mapper.mapToFilm(updateFilmDto);
        final FilmDto filmDto = filmService.updateFilm(film).map(mapper::mapToDto).orElseThrow(
                () -> new NotFoundException(Film.class, updateFilmDto.getId())
        );
        log.info("Responded to PUT /films: {}", filmDto);
        return filmDto;
    }
}
