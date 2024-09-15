package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

@Mapper
public interface EventMapper {

    Collection<EventDto> mapToDto(Collection<Event> events);
}
