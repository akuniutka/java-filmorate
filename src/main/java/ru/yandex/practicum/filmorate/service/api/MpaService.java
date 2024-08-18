package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaService {

    Collection<Mpa> getMpas();

    Optional<Mpa> getMpa(long id);
}
