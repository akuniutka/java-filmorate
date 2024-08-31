package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

@Mapper
public abstract class EventMapper {

    public EventDto mapToDto(Event event) {
        EventDto dto = new EventDto();
        dto.setEventId(event.getId());
        dto.setUserId(event.getUserId());
        dto.setEventType(event.getEventType().name());
        dto.setOperation(event.getOperation().name());
        dto.setEntityId(event.getEntityId());
        dto.setTimestamp(event.getTimestamp().toEpochMilli());
        return dto;
    }

    public abstract Collection<EventDto> mapToDto(Collection<Event> events);
}
