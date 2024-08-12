package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.api.GenreService;
import ru.yandex.practicum.filmorate.storage.api.GenreStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
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
    public Optional<Genre> getGenre(Long id) {
        return genreStorage.findById(id);
    }

    @Override
    public Collection<Genre> getGenresByFilmId(Long filmId) {
        return genreStorage.findAllByFilmId(filmId);
    }

    @Override
    public void updateFilmGenres(Long filmId, Collection<Genre> genres) {
        Set<Genre> newGenres = new HashSet<>(genres);
        Set<Genre> oldGenres = new HashSet<>(getGenresByFilmId(filmId));
        oldGenres.stream()
                .filter(genre -> !newGenres.contains(genre))
                .forEach(genre -> genreStorage.deleteFilmGenre(filmId, genre));
        newGenres.stream()
                .filter(genre -> !oldGenres.contains(genre))
                .forEach(genre -> genreStorage.saveFilmGenre(filmId, genre));
    }
}
