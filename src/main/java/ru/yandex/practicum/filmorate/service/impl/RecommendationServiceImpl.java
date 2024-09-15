package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.api.FilmService;
import ru.yandex.practicum.filmorate.service.api.RecommendationService;
import ru.yandex.practicum.filmorate.service.api.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final UserService userService;
    private final FilmService filmService;

    @Override
    public Collection<Film> getRecommended(final long userId) {
        final User user = userService.getUser(userId);
        final Map<Film, Integer> userLikes = userService.getLikes(userId);
        final Set<User> users = new HashSet<>(userService.getUsers());
        users.remove(user);
        int maxCommonFilms = 1;
        long minLikesDistance = Long.MAX_VALUE;
        User closestUser = null;
        Map<Film, Integer> closestLikes = Collections.emptyMap();

        for (User anotherUser : users) {
            Collection<Film> commonFilms = filmService.getCommonFilms(userId, anotherUser.getId());
            if (commonFilms.size() >= maxCommonFilms) {
                Map<Film, Integer> anotherUserLikes = userService.getLikes(anotherUser.getId());
                long likesDistance = likesDistance(userLikes, anotherUserLikes, commonFilms);
                if (commonFilms.size() > maxCommonFilms || likesDistance < minLikesDistance) {
                    maxCommonFilms = commonFilms.size();
                    minLikesDistance = likesDistance;
                    closestUser = anotherUser;
                    closestLikes = anotherUserLikes;
                }
            }
        }
        final Map<Film, Integer> f = closestLikes;

        return closestUser == null ? Collections.emptySet() : filmService.getLikedFilms(closestUser.getId()).stream()
                .filter(film -> !userLikes.containsKey(film))
                .filter(film -> f.get(film) > 5)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private long likesDistance(
            final Map<Film, Integer> userLikes,
            final Map<Film, Integer> anotherUserLikes,
            final Collection<Film> commonFilms
    ) {
        long likesDistance = 0L;
        for (Film film : commonFilms) {
            int userMark = userLikes.get(film);
            int anotherUserMark = anotherUserLikes.get(film);
            likesDistance += Math.abs(userMark - anotherUserMark);
        }
        return likesDistance;
    }
}
