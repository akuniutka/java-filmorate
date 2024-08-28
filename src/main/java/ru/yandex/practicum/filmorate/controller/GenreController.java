package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.service.api.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {

    private final GenreService genreService;
    private final GenreMapper mapper;

    @GetMapping("/{id}")
    public GenreDto getGenre(@PathVariable final long id) {
        log.info("Received GET at /genres/{}", id);
        final GenreDto dto = mapper.mapToDto(genreService.getGenre(id));
        log.info("Responded to GET /genres/{}: {}", id, dto);
        return dto;
    }

    @GetMapping
    public Collection<GenreDto> getGenres() {
        log.info("Received GET at /genres");
        final Collection<GenreDto> dtos = mapper.mapToDto(genreService.getGenres());
        log.info("Responded to GET /genres: {}", dtos);
        return dtos;
    }
}
