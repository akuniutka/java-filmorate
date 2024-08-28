package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreService {

    Collection<Genre> getGenres();

    Genre getGenre(long id);

    void validateId(Collection<Long> ids);
}
