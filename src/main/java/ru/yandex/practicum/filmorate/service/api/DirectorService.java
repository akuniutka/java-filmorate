package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {

    Collection<Director> getDirectors();

    Director getDirector(long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(long id);

    void validateId(Collection<Long> ids);
}
