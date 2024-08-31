package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.api.DirectorService;
import ru.yandex.practicum.filmorate.storage.api.DirectorStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public Collection<Director> getDirectors() {
        return directorStorage.findAll();
    }

    @Override
    public Director getDirector(final long id) {
        return directorStorage.findById(id).orElseThrow(
                () -> new NotFoundException(Director.class, id)
        );
    }

    @Override
    public Director createDirector(final Director director) {
        Objects.requireNonNull(director, "Cannot create director: is null");
        final Director savedDirector = directorStorage.save(director);
        log.info("Created new director: {}", savedDirector);
        return savedDirector;
    }

    @Override
    public Director updateDirector(final Director director) {
        Objects.requireNonNull(director, "Cannot update director: is null");
        final Optional<Director> savedDirector = directorStorage.update(director);
        savedDirector.ifPresent(d -> log.info("Updated director: {}", d));
        return savedDirector.orElseThrow(
                () -> new NotFoundException(Director.class, director.getId())
        );
    }

    @Override
    public void deleteDirector(final long id) {
        directorStorage.delete(id);
        log.info("Deleted director with id = {}", id);
    }

    @Override
    public void validateId(final Collection<Long> ids) {
        Set<Long> distinctIds = new HashSet<>(ids);
        directorStorage.findById(ids).forEach(director -> distinctIds.remove(director.getId()));
        if (!distinctIds.isEmpty()) {
            throw new ValidationException("Check that director id is correct (you sent %s"
                    .formatted(distinctIds.iterator().next()));
        }
    }
}
