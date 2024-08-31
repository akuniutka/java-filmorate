package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.api.DirectorStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.TestModels.assertDirectorEquals;
import static ru.yandex.practicum.filmorate.TestModels.assertDirectorListEquals;
import static ru.yandex.practicum.filmorate.TestModels.cloneDirector;
import static ru.yandex.practicum.filmorate.TestModels.getRandomDirector;

public abstract class AbstractDirectorStorageTest {

    protected DirectorStorage directorStorage;

    @AfterEach
    void tearDown() {
        directorStorage.deleteAll();
    }

    @Test
    void shouldReturnCorrectDirectorList() {
        final List<Director> expectedDirectors = preloadData();

        final List<Director> actualDirectors = new ArrayList<>(directorStorage.findAll());

        assertDirectorListEquals(expectedDirectors, actualDirectors);
    }

    @Test
    void shouldReturnCorrectDirectorById() {
        final List<Director> expectedDirectors = preloadData();

        final Optional<Director> actual = directorStorage.findById(expectedDirectors.get(1).getId());

        assertTrue(actual.isPresent(), "optional should no be empty");
        assertDirectorEquals(expectedDirectors.get(1), actual.get());
    }

    @Test
    void shouldReturnEmptyOptionalForUnknownId() {
        final List<Director> expectedDirectors = preloadData();
        final long id = expectedDirectors.getFirst().getId() - 1;

        final Optional<Director> actual = directorStorage.findById(id);

        assertTrue(actual.isEmpty(), "should find no director for id = " + id);
    }

    @Test
    void shouldUpdateDirector() {
        final List<Director> expectedDirectors = preloadData();
        final long id = expectedDirectors.get(1).getId();
        final Director expectedDirector = getRandomDirector();
        expectedDirector.setId(id);

        final Director savedDirector = directorStorage.update(cloneDirector(expectedDirector)).orElseThrow();
        final Director actualDirector = directorStorage.findById(id).orElseThrow();

        assertDirectorEquals(expectedDirector, savedDirector);
        assertDirectorEquals(expectedDirector, actualDirector);
    }

    @Test
    void shouldReturnEmptyOptionalWhenUpdateUnknownDirector() {
        final List<Director> expectedDirectors = preloadData();
        final Director expectedDirector = getRandomDirector();
        expectedDirector.setId(expectedDirectors.getFirst().getId() - 1);

        final Optional<Director> savedDirector = directorStorage.update(cloneDirector(expectedDirector));

        assertTrue(savedDirector.isEmpty(), "should find no director to update with id = " + expectedDirector.getId());
    }

    @Test
    void shouldDeleteDirector() {
        final List<Director> expectedDirectors = preloadData();
        final long id = expectedDirectors.get(1).getId();
        expectedDirectors.remove(1);

        directorStorage.delete(id);
        final List<Director> actualDirectors = new ArrayList<>(directorStorage.findAll());

        assertDirectorListEquals(expectedDirectors, actualDirectors);
    }

    @Test
    void shouldDeleteAllDirectors() {
        preloadData();

        directorStorage.deleteAll();
        final Collection<Director> actual = directorStorage.findAll();

        assertTrue(actual.isEmpty(), "should no director remain");
    }

    protected List<Director> preloadData() {
        final List<Director> expectedDirectors = new ArrayList<>();
        final List<Director> savedDirectors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Director director = getRandomDirector();
            Director savedDirector = directorStorage.save(cloneDirector(director));
            director.setId(savedDirector.getId());
            expectedDirectors.add(director);
            savedDirectors.add(savedDirector);
        }
        assertDirectorListEquals(expectedDirectors, savedDirectors);
        return expectedDirectors;
    }
}
