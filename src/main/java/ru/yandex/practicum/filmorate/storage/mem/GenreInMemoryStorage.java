package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class GenreInMemoryStorage implements GenreStorage {

    private final Map<Long, Genre> genres;

    public GenreInMemoryStorage() {
        this.genres = getGenres();
    }

    @Override
    public Collection<Genre> findAll() {
        return genres.values().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .toList();
    }

    @Override
    public Optional<Genre> findById(final long id) {
        return Optional.ofNullable(genres.get(id));
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
