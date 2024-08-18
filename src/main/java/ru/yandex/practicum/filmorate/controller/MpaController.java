package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.api.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController {

    private final MpaService mpaService;
    private final MpaMapper mapper;

    @GetMapping("/{id}")
    public MpaDto getMpa(@PathVariable final long id) {
        log.info("Received GET at /mpa/{}", id);
        final MpaDto dto = mpaService.getMpa(id).map(mapper::mapToDto).orElseThrow(
                () -> new NotFoundException(Mpa.class, id)
        );
        log.info("Responded to GET /mpa/{}: {}", id, dto);
        return dto;
    }

    @GetMapping
    public Collection<MpaDto> getMpas() {
        log.info("Received GET at /mpa");
        final Collection<MpaDto> dtos = mapper.mapToDto(mpaService.getMpas());
        log.info("Responded to GET /mpa: {}", dtos);
        return dtos;
    }
}
