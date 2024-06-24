package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Received POST at /films: {}", film);
        final Film savedFilm = filmService.create(film);
        log.info("Responded to POST at /films: {}", savedFilm);
        return savedFilm;
    }

    @GetMapping
    public Collection<Film> finaAll() {
        log.info("Received GET at /films");
        final Collection<Film> films = filmService.findAll();
        log.info("Responded to GET at /films: {}", films);
        return films;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Received PUT at /films: {}", film);
        final Film savedFilm = filmService.update(film);
        log.info("Responded to PUT at /films: {}", savedFilm);
        return savedFilm;
    }
}
