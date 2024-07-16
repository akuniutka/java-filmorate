package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final String modelName;
    private final Long modelId;

    public NotFoundException(String modelName, Long modelId) {
        super("Cannot find model '%s' with id = %s".formatted(modelName, modelId));
        this.modelName = modelName;
        this.modelId = modelId;
    }
}
