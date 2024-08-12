package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmMpa;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.api.MpaService;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaMapper {

    private final MpaService mpaService;

    public Mpa mapToMpa(final FilmMpa dto) {
        return mpaService.getMpa(dto.getId()).orElseThrow(
                () -> new ValidationException("Check that genre id is correct (you sent %s)".formatted(dto.getId()))
        );
    }

    public MpaDto mapToDto(final Mpa mpa) {
        final MpaDto dto = new MpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }

    public Collection<MpaDto> mapToDto(final Collection<Mpa> mpas) {
        return mpas.stream()
                .map(this::mapToDto)
                .toList();
    }
}
