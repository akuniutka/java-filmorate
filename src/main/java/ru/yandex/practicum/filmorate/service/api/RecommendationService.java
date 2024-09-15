package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface RecommendationService {

    Collection<Film> getRecommended(long userId);
}
