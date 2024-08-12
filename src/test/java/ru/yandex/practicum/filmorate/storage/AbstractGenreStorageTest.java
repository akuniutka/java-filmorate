package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractGenreStorageTest {

    protected GenreStorage genreStorage;

    @Test
    void shouldReturnCorrectGenreList() {
        final String[] genreNames = {"Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик"};
        final List<Genre> expected = new ArrayList<>();
        for (int i = 0; i < genreNames.length; i++) {
            Genre genre = new Genre();
            genre.setId(i + 1L);
            genre.setName(genreNames[i]);
            expected.add(genre);
        }

        final List<Genre> actual = new ArrayList<>(genreStorage.findAll());

        assertEquals(expected.size(), actual.size(), "wrong number of genres");
        for (int i = 0; i < expected.size(); i++) {
            assertGenreEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    void shouldReturnCorrectGenreById() {
        final Genre expected = new Genre();
        expected.setId(4L);
        expected.setName("Триллер");

        final Optional<Genre> actual = genreStorage.findById(expected.getId());

        assertTrue(actual.isPresent(), "optional should not be empty");
        assertGenreEquals(expected, actual.get());
    }

    @Test
    void shouldReturnEmptyOptionalForUnknownId() {
        final Long id = -1L;

        final Optional<Genre> actual = genreStorage.findById(id);

        assertTrue(actual.isEmpty(), "should find no genre for id = " + 1d);
    }

    protected void assertGenreEquals(final Genre expected, final Genre actual) {
        if (expected == null) {
            throw new IllegalArgumentException("value to check against should not be null");
        }
        assertNotNull(actual, "should be not null");
        assertAll("wrong genre",
                () -> assertEquals(expected.getId(), actual.getId(), "wrong genre id"),
                () -> assertEquals(expected.getName(), actual.getName(), "wrong genre name")
        );
    }
}
