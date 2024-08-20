package ru.yandex.practicum.filmorate.mapper;


import ru.yandex.practicum.filmorate.dto.RewiewDto;
import ru.yandex.practicum.filmorate.model.Rewiew;

import java.util.Collection;

public interface RewiewMapper {

//    Rewiew mapToRewiew(NewRewiewDto dto);
//
//    Rewiew mapToRewiew(UpdateRewiewDto dto);

    RewiewDto mapToDto(Rewiew rewiew);

    Collection<RewiewDto> mapToDto(Collection<Rewiew> rewiews);
}
