package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.service.api.RecommendationService;
import ru.yandex.practicum.filmorate.service.api.UserService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final UserService userService;
    private final FilmService filmService;

    @Override
    public Collection<Film> getRecommended(final long userId) {
        userService.getUser(userId);
        return filmService.getRecommended(userId);
    }
}
