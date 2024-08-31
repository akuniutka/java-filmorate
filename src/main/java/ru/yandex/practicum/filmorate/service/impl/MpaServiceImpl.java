package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.api.MpaService;
import ru.yandex.practicum.filmorate.storage.api.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public Collection<Mpa> getMpas() {
        return mpaStorage.findAll();
    }

    @Override
    public Mpa getMpa(final long id) {
        return mpaStorage.findById(id).orElseThrow(
                () -> new NotFoundException(Mpa.class, id)
        );
    }

    @Override
    public void validateId(final long id) {
        mpaStorage.findById(id).orElseThrow(
                () -> new ValidationException("Check that mpa id is correct (you sent %s)".formatted(id))
        );
    }
}
