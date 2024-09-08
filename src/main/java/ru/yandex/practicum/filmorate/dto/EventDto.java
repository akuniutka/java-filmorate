package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"id"})
public class EventDto {

    @JsonProperty("eventId")
    private Long id;

    private Long userId;
    private String eventType;
    private String operation;
    private Long entityId;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Instant timestamp;
}

