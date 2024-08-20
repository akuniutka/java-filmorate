package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.RewiewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RewiewMapper;
import ru.yandex.practicum.filmorate.model.Rewiew;
import ru.yandex.practicum.filmorate.service.api.RewiewService;

@RestController
@RequestMapping("/rewiews")
@RequiredArgsConstructor
@Slf4j
public class RewiewController {

    private final RewiewService rewiewService;
    private final RewiewMapper mapper;

    @GetMapping("/{id}")
    public RewiewDto getRewiew(@PathVariable final long id) {
        log.info("Received GET at /rewiews/{}", id);
        final RewiewDto dto = rewiewService.getRewiew(id).map(mapper::mapToDto).orElseThrow(
                () -> new NotFoundException(Rewiew.class, id)
        );
        log.info("Responded to GET /films/{}: {}", id, dto);
        return dto;
    }
}
