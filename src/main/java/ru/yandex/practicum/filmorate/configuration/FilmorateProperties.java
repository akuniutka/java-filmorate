package ru.yandex.practicum.filmorate.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("filmorate")
@Getter
@Setter
@ToString
public class FilmorateProperties {

    private StorageProperties storage;

    @Getter
    @Setter
    @ToString
    public static class StorageProperties {
        private StorageMode mode;
    }

    public enum StorageMode {
        MEMORY,
        DATABASE
    }
}
