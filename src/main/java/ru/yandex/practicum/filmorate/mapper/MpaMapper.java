package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmMpaDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Mapper
public interface MpaMapper {

    MpaDto mapToDto(Mpa mpa);

    Collection<MpaDto> mapToDto(Collection<Mpa> mpas);

    Mpa mapToMpa(final FilmMpaDto dto);
}
