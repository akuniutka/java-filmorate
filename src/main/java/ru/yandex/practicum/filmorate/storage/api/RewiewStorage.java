package ru.yandex.practicum.filmorate.storage.api;

import ru.yandex.practicum.filmorate.model.Rewiew;

import java.util.Collection;
import java.util.Optional;

public interface RewiewStorage {
    Collection<Rewiew> findAll();

    Optional<Rewiew> findById(long id);

//    Rewiew save(Rewiew rewiew);
//
//    Optional<Rewiew> update(Rewiew rewiew);
//
//    void deleteRewiew(long id);
//
//    void addLikeToRewiew(long id, long friendId);
//
//    void addDislikeToRewiew(long id, long userId);
//
//    void deleteLikeToRewiew(long id, long userId);
//
//    void deleteDislikeToRewiew(long id, long userId);
}
