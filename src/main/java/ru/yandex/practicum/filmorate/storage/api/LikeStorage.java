package ru.yandex.practicum.filmorate.storage.api;

import java.util.Collection;

public interface LikeStorage {

    void save(Long filmId, Long userId);

    void delete(Long filmId, Long userId);

    Collection<Long> findAllFilmIdOrderByLikesDesc(Long limit);
}
