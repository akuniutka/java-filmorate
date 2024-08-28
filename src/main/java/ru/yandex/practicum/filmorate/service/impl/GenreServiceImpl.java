package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.api.GenreService;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {

    private final GenreStorage genreStorage;

    @Override
    public Collection<Genre> getGenres() {
        return genreStorage.findAll();
    }

    @Override
    public Genre getGenre(final long id) {
        return genreStorage.findById(id).orElseThrow(
                () -> new NotFoundException(Genre.class, id)
        );
    }

    @Override
    public void validateId(final Collection<Long> ids) {
        Set<Long> distinctIds = new HashSet<>(ids);
        genreStorage.findById(ids).forEach(genre -> distinctIds.remove(genre.getId()));
        if (!distinctIds.isEmpty()) {
            throw new ValidationException("Check that genre id is correct (you sent %s)"
                    .formatted(distinctIds.iterator().next()));
        }
    }
}
