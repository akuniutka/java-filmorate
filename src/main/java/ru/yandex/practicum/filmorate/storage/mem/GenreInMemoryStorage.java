package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class GenreInMemoryStorage extends BaseInMemoryStorage<Genre> implements GenreStorage {

    public GenreInMemoryStorage() {
        super(Genre::getId, Genre::setId);
        this.data.putAll(getGenres());
    }

    private Map<Long, Genre> getGenres() {
        String[] genreNames = {"Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик"};
        Map<Long, Genre> genres = new HashMap<>();
        for (int i = 0; i < genreNames.length; i++) {
            Genre genre = new Genre();
            genre.setId(i + 1L);
            genre.setName(genreNames[i]);
            genres.put(genre.getId(), genre);
        }
        return genres;
    }
}
