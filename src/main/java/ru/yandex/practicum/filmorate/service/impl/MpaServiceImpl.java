package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.api.MpaService;
import ru.yandex.practicum.filmorate.storage.api.MpaStorage;

import java.util.Collection;
import java.util.Optional;

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
    public Optional<Mpa> getMpa(final Long id) {
        return mpaStorage.findById(id);
    }
}
