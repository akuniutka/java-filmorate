package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"eventId"})
public class EventDto {
    private Long eventId;
    private Long userId;
    private String eventType;
    private String operation;
    private Long entityId;
    private Long timestamp;
}

