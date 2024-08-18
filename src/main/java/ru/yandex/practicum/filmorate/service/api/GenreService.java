package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreService {

    Collection<Genre> getGenres();

    Optional<Genre> getGenre(long id);
}
