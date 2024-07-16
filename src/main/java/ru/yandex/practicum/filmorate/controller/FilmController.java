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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable final Long id, @PathVariable final Long userId) {
        log.info("Received PUT at /films/{}/like{}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable final Long id, @PathVariable final Long userId) {
        log.info("Received DELETE at /films/{}/like/{}", id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getTopLiked(@RequestParam(defaultValue = "10") @Positive final Long count) {
        log.info("Received GET at /films/popular (count = {})", count);
        final Collection<Film> films = filmService.getTopLiked(count);
        log.info("Responded to GET /films/popular (count = {}): {}", count, films);
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable final Long id) {
        log.info("Received GET at /films/{}", id);
        final Film film = filmService.findFilmById(id);
        log.info("Responded to GET /films{}: {}", id, film);
        return film;
    }

    @PostMapping
    public Film create(@Valid @RequestBody final Film film) {
        log.info("Received POST at /films: {}", film);
        final Film createdFilm = filmService.create(film);
        log.info("Responded to POST /films: {}", createdFilm);
        return createdFilm;
    }

    @GetMapping
    public Collection<Film> finaAll() {
        log.info("Received GET at /films");
        final Collection<Film> films = filmService.findAll();
        log.info("Responded to GET /films: {}", films);
        return films;
    }

    @PutMapping
    public Film update(@Valid @RequestBody final Film film) {
        log.info("Received PUT at /films: {}", film);
        final Film updatedFilm = filmService.update(film);
        log.info("Responded to PUT /films: {}", updatedFilm);
        return updatedFilm;
    }
}
