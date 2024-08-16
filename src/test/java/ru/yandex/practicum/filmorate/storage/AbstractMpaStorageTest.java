package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.api.MpaStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractMpaStorageTest {

    protected MpaStorage mpaStorage;

    @Test
    void shouldReturnCorrectMPAList() {
        final String[] mpaNames = {"G", "PG", "PG-13", "R", "NC-17" };
        final List<Mpa> expected = new ArrayList<>();
        for (int i = 0; i < mpaNames.length; i++) {
            Mpa mpa = new Mpa();
            mpa.setId(i + 1L);
            mpa.setName(mpaNames[i]);
            expected.add(mpa);
        }

        final List<Mpa> actual = new ArrayList<>(mpaStorage.findAll());

        assertEquals(expected.size(), actual.size(), "wrong number of MPA ratings");
        for (int i = 0; i < expected.size(); i++) {
            assertMpaEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    void shouldReturnCorrectMPAById() {
        final Mpa expected = new Mpa();
        expected.setId(5L);
        expected.setName("NC-17");

        final Optional<Mpa> actual = mpaStorage.findById(expected.getId());

        assertTrue(actual.isPresent(), "Optional should not be empty");
        assertMpaEquals(expected, actual.get());
    }

    @Test
    void shouldReturnEmptyOptionalForUnknownId() {
        final Long id = -1L;

        final Optional<Mpa> actual = mpaStorage.findById(id);

        assertTrue(actual.isEmpty(), "should find no mpa for id = " + id);
    }

    protected void assertMpaEquals(final Mpa expected, final Mpa actual) {
        if (expected == null) {
            throw new IllegalArgumentException("value to check against should not be null");
        }
        assertNotNull(actual, "should be not null");
        assertAll("wrong MPA",
                () -> assertEquals(expected.getId(), actual.getId(), "wrong MPA id"),
                () -> assertEquals(expected.getName(), actual.getName(), "wrong MPA name")
        );
    }

}
