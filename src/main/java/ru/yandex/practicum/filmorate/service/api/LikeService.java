package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface LikeService {

    void createLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    Collection<Film> getTopFilmsByLikes(Long limit);
}
