package ru.yandex.practicum.filmorate.service.api;

import ru.yandex.practicum.filmorate.model.Rewiew;

import java.util.Collection;
import java.util.Optional;

public interface RewiewService {
    Collection<Rewiew> getRewiews();

    Optional<Rewiew> getRewiew(long id);

    Rewiew createRewiew(Rewiew rewiew);

    Optional<Rewiew> updateRewiew(Rewiew rewiew);

    void deleteRewiew(long id);

    void addLikeToRewiew(long id, long friendId);

    void addDislikeToRewiew(long id, long userId);

    void deleteLikeToRewiew(long id, long userId);

    void deleteDislikeToRewiew(long id, long userId);
}

