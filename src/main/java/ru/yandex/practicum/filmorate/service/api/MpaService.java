package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaService {

    Collection<Mpa> getMpas();

    Mpa getMpa(long id);

    void validateId(long id);
}
