package ru.yandex.practicum.filmorate.storage.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.api.LikeStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class LikeInMemoryStorage implements LikeStorage {

    private final Map<Long, Set<Long>> likesByUserId;
    private final Map<Long, Set<Long>> likesByFilmId;

    public LikeInMemoryStorage() {
        this.likesByUserId = new HashMap<>();
        this.likesByFilmId = new HashMap<>();
    }

    @Override
    public void save(final Long filmId, final Long userId) {
        Objects.requireNonNull(filmId, "Cannot save like: film is null");
        Objects.requireNonNull(userId, "Cannot save like: user is null");
        likesByFilmId.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        likesByUserId.computeIfAbsent(userId, k -> new HashSet<>()).add(filmId);
    }

    @Override
    public void delete(final Long filmId, final Long userId) {
        Objects.requireNonNull(filmId, "Cannot delete like: film is null");
        Objects.requireNonNull(userId, "Cannot delete like: user is null");
        final Set<Long> filmLikes = likesByFilmId.get(filmId);
        if (filmLikes != null) {
            filmLikes.remove(userId);
        }
        final Set<Long> userLikes = likesByUserId.get(userId);
        if (userLikes != null) {
            userLikes.remove(filmId);
        }
    }

    @Override
    public Collection<Long> findAllFilmIdOrderByLikesDesc(final Long limit) {
        return likesByFilmId.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().size() - e1.getValue().size())
                .map(Map.Entry::getKey)
                .limit(limit)
                .toList();
    }
}
