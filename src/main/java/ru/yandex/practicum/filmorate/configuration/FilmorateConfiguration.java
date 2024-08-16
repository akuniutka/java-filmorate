package ru.yandex.practicum.filmorate.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.yandex.practicum.filmorate.storage.api.FriendStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.mem.FilmInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mem.FriendInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.mem.GenreInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.mem.LikeInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.api.LikeStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.api.MpaStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mem.MpaInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.mem.UserInMemoryStorage;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

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

    private final FriendInMemoryStorage friendInMemoryStorage;
    private final FriendDbStorage friendDbStorage;

    private final LikeInMemoryStorage likeInMemoryStorage;
    private final LikeDbStorage likeDbStorage;

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

    @Bean
    @Primary
    public FriendStorage friendStorage() {
        if (props.getStorage() == null) {
            return friendInMemoryStorage;
        }
        return switch (props.getStorage().getMode()) {
            case DATABASE -> friendDbStorage;
            case MEMORY -> friendInMemoryStorage;
            case null -> friendInMemoryStorage;
        };
    }

    @Bean
    @Primary
    public LikeStorage likeStorage() {
        if (props.getStorage() == null) {
            return likeInMemoryStorage;
        }
        return switch (props.getStorage().getMode()) {
            case DATABASE -> likeDbStorage;
            case MEMORY -> likeInMemoryStorage;
            case null -> likeInMemoryStorage;
        };
    }
}
