package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorService {

    Collection<Director> getDirectors();

    Optional<Director> getDirector(long id);

    Director createDirector(Director director);

    Optional<Director> updateDirector(Director director);

    void deleteDirector(long id);

    void assertDirectorExists(long id);
}
