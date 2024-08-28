package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.NewDirectorDto;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.api.DirectorService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;
    private final DirectorMapper mapper;

    @GetMapping("/{id}")
    public DirectorDto getDirector(@PathVariable final long id) {
        log.info("Received GET at /directors/{}", id);
        final DirectorDto dto = mapper.mapToDto(directorService.getDirector(id));
        log.info("Responded to GET /directors/{}: {}", id, dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable final long id) {
        log.info("Received DELETE at /directors/{}", id);
        directorService.deleteDirector(id);
        log.info("Responded to DELETE /directors/{} with no body", id);
    }

    @PostMapping
    public DirectorDto createDirector(@Valid @RequestBody final NewDirectorDto newDirectorDto) {
        log.info("Received POST at /directors: {}", newDirectorDto);
        final Director director = mapper.mapToDirector(newDirectorDto);
        final DirectorDto directorDto = mapper.mapToDto(directorService.createDirector(director));
        log.info("Responded to POST /directors: {}", directorDto);
        return directorDto;
    }

    @PutMapping
    public DirectorDto updateDirector(@Valid @RequestBody final UpdateDirectorDto updateDirectorDto) {
        log.info("Received PUT at /directors: {}", updateDirectorDto);
        final Director director = mapper.mapToDirector(updateDirectorDto);
        final DirectorDto directorDto = mapper.mapToDto(directorService.updateDirector(director));
        log.info("Responded to PUT /directors: {}", directorDto);
        return directorDto;
    }

    @GetMapping
    public Collection<DirectorDto> getDirectors() {
        log.info("Received GET at /directors");
        final Collection<DirectorDto> dtos = mapper.mapToDto(directorService.getDirectors());
        log.info("Responded to GET /directors: {}", dtos);
        return dtos;
    }
}
