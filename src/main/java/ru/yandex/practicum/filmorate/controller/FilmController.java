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
import ru.yandex.practicum.filmorate.service.api.LikeService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final LikeService likeService;
    private final FilmMapper mapper;

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable final Long id, @PathVariable final Long userId) {
        log.info("Received PUT at /films/{}/like/{}", id, userId);
        likeService.createLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable final Long id, @PathVariable final Long userId) {
        log.info("Received DELETE at /films/{}/like/{}", id, userId);
        likeService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getTopLiked(@RequestParam(defaultValue = "10") @Valid @Positive final Long count) {
        log.info("Received GET at /films/popular (count = {})", count);
        final Collection<FilmDto> dtos = mapper.mapToDto(likeService.getTopFilmsByLikes(count));
        log.info("Responded to GET /films/popular (count = {}): {}", count, dtos);
        return dtos;
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable final Long id) {
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
