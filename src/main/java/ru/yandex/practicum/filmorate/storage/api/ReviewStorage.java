package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Collection<Review> findAll();

    Optional<Review> findById(long id);

     Review save(Review review);

    Optional<Review> update(Review review);

   void delete(long id);
//
//    void addLikeToRewiew(long id, long friendId);
//
//    void addDislikeToRewiew(long id, long userId);
//
//    void deleteLikeToRewiew(long id, long userId);
//
//    void deleteDislikeToRewiew(long id, long userId);
}
