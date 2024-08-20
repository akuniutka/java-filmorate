package ru.yandex.practicum.filmorate.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.filmorate.storage.api.*;
import ru.yandex.practicum.filmorate.storage.db.*;
import ru.yandex.practicum.filmorate.storage.mem.FilmInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.mem.GenreInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.mem.MpaInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.mem.UserInMemoryStorage;

@Configuration
@RequiredArgsConstructor
public class FilmorateConfiguration {

    private final FilmorateProperties props;

    private final GenreInMemoryStorage genreInMemoryStorage;
    private final GenreDbStorage genreDbStorage;

    private final MpaInMemoryStorage mpaInMemoryStorage;
    private final MpaDbStorage mpaDbStorage;

    private final FilmInMemoryStorage filmInMemoryStorage;
    private final FilmDbStorage filmDbStorage;

    private final UserInMemoryStorage userInMemoryStorage;
    private final UserDbStorage userDbStorage;

    private final ReviewDbStorage reviewDbStorage;

    @Bean
    @Primary
    public ReviewStorage rewiewStorageStorage() {
        if (props.getStorage() == null) {
            return reviewDbStorage;
        }
        return switch (props.getStorage().getMode()) {
            case DATABASE -> reviewDbStorage;
            case MEMORY -> reviewDbStorage;
            case null -> reviewDbStorage;
        };
    }


    @Bean
    @Primary
    public GenreStorage genreStorage() {
        if (props.getStorage() == null) {
            return genreInMemoryStorage;
        }
        return switch (props.getStorage().getMode()) {
            case DATABASE -> genreDbStorage;
            case MEMORY -> genreInMemoryStorage;
            case null -> genreInMemoryStorage;
        };
    }

    @Bean
    @Primary
    public MpaStorage mpaStorage() {
        if (props.getStorage() == null) {
            return mpaInMemoryStorage;
        }
        return switch (props.getStorage().getMode()) {
            case DATABASE -> mpaDbStorage;
            case MEMORY -> mpaInMemoryStorage;
            case null -> mpaInMemoryStorage;
        };
    }

    @Bean
    @Primary
    public FilmStorage filmStorage() {
        if (props.getStorage() == null) {
            return filmInMemoryStorage;
        }
        return switch (props.getStorage().getMode()) {
            case DATABASE -> filmDbStorage;
            case MEMORY -> filmInMemoryStorage;
            case null -> filmInMemoryStorage;
        };
    }

    @Bean
    @Primary
    public UserStorage userStorage() {
        if (props.getStorage() == null) {
            return userInMemoryStorage;
        }
        return switch (props.getStorage().getMode()) {
            case DATABASE -> userDbStorage;
            case MEMORY -> userInMemoryStorage;
            case null -> userInMemoryStorage;
        };
    }
}
