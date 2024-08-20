package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rewiew;
import ru.yandex.practicum.filmorate.service.api.RewiewService;
import ru.yandex.practicum.filmorate.service.api.UserService;
import ru.yandex.practicum.filmorate.storage.api.FilmStorage;
import ru.yandex.practicum.filmorate.storage.api.RewiewStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewiewServiceImpl implements RewiewService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final RewiewStorage rewiewStorage;


    @Override
    public Collection<Rewiew> getRewiews() {
        return rewiewStorage.findAll();
    }

    @Override
    public Optional<Rewiew> getRewiew(final long id) {
        return rewiewStorage.findById(id);
    }
}
