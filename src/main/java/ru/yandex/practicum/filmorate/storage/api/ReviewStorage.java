package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {

    Review save(Review review);

    Optional<Review> findById(long id);

    Collection<Review> findAllOrderByUsefulDesc(long count);

    Collection<Review> findAllByFilmIdOrderByUsefulDesc(long filmId, long count);

    Optional<Review> update(Review review);

    Review addLike(long id, long userId);

    Review addDislike(long id, long userId);

    Review deleteLike(long id, long userId);

    Review deleteDislike(long id, long userId);

    boolean delete(long id);
}
