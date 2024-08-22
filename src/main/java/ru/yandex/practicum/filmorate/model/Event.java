package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"id"})
public class Event {
    private Long id;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long eventId;
    private Long entityId;
    private Instant timestamp;
}
