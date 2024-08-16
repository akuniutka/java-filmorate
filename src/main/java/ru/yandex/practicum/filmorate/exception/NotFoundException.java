package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final String modelName;
    private final Long modelId;

    public NotFoundException(final String modelName, final Long modelId) {
        super("Cannot find model '%s' with id = %s".formatted(modelName, modelId));
        this.modelName = modelName;
        this.modelId = modelId;
    }

    public <T> NotFoundException(final Class<T> modelClass, final Long modelId) {
        this(modelClass.getSimpleName().toLowerCase(), modelId);
    }
}
