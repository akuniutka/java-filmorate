package ru.yandex.practicum.filmorate.mapper;


import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.RewiewDto;
import ru.yandex.practicum.filmorate.model.Rewiew;

import java.util.Collection;

@Mapper
public interface RewiewMapper {

   Rewiew mapToRewiew(RewiewDto dto);
//
//    Rewiew mapToRewiew(UpdateRewiewDto dto);

    RewiewDto mapToDto(Rewiew rewiew);

    Collection<RewiewDto> mapToDto(Collection<Rewiew> rewiews);
}
