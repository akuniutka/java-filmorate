package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
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
public class DirectorController extends BaseController {

    private final DirectorService directorService;
    private final DirectorMapper mapper;

    @PostMapping
    public DirectorDto createDirector(
            @Valid @RequestBody final NewDirectorDto newDirectorDto,
            final HttpServletRequest request
    ) {
        logRequest(request, newDirectorDto);
        final Director director = mapper.mapToDirector(newDirectorDto);
        final DirectorDto directorDto = mapper.mapToDto(directorService.createDirector(director));
        logResponse(request, directorDto);
        return directorDto;
    }

    @GetMapping("/{id}")
    public DirectorDto getDirector(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final DirectorDto dto = mapper.mapToDto(directorService.getDirector(id));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    public Collection<DirectorDto> getDirectors(
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Collection<DirectorDto> dtos = mapper.mapToDto(directorService.getDirectors());
        logResponse(request, dtos);
        return dtos;
    }

    @PutMapping
    public DirectorDto updateDirector(
            @Valid @RequestBody final UpdateDirectorDto updateDirectorDto,
            final HttpServletRequest request
    ) {
        logRequest(request, updateDirectorDto);
        final Director director = mapper.mapToDirector(updateDirectorDto);
        final DirectorDto directorDto = mapper.mapToDto(directorService.updateDirector(director));
        logResponse(request, directorDto);
        return directorDto;
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        directorService.deleteDirector(id);
        logResponse(request);
    }
}
