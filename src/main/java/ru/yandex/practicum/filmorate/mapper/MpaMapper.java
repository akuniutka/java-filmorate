package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.FilmMpa;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.api.MpaService;

import java.util.Collection;

@Mapper
public abstract class MpaMapper {

    private MpaService mpaService;

    @Autowired
    public void setMpaService(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    public abstract MpaDto mapToDto(Mpa mpa);

    public abstract Collection<MpaDto> mapToDto(Collection<Mpa> mpas);

    public Mpa mapToMpa(final FilmMpa dto) {
        return dto == null ? null : mpaService.getMpa(dto.getId()).orElseThrow(
                () -> new ValidationException("Check that mpa id is correct (you sent %s)".formatted(dto.getId()))
        );
    }
}
