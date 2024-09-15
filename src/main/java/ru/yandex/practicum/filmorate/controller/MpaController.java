package ru.yandex.practicum.filmorate.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.service.api.MpaService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class MpaController extends BaseController {

    private final MpaService mpaService;
    private final MpaMapper mapper;

    @GetMapping("/{id}")
    public MpaDto getMpa(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final MpaDto dto = mapper.mapToDto(mpaService.getMpa(id));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    public Collection<MpaDto> getMpas(
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Collection<MpaDto> dtos = mapper.mapToDto(mpaService.getMpas());
        logResponse(request, dtos);
        return dtos;
    }
}
