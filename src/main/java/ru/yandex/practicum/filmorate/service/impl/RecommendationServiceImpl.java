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
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final UserService userService;
    private final FilmService filmService;

    @Override
    public Collection<Film> getRecommended(final long userId) {
        final User user = userService.getUser(userId);
        final Collection<Film> userLikedFilms = filmService.getLikedFilms(userId);
        final Set<User> users = new HashSet<>(userService.getUsers());
        users.remove(user);
        int maxCommonFilms = 1;
        Collection<Film> maxAnotherUserLikedFilms = Collections.emptySet();

        for (User anotherUser : users) {
            Collection<Film> commonFilms = filmService.getCommonFilms(userId, anotherUser.getId());
            if (commonFilms.size() >= maxCommonFilms) {
                maxCommonFilms = commonFilms.size();
                Collection<Film> anotherUserLikedFilms = filmService.getLikedFilms(anotherUser.getId());
                if (anotherUserLikedFilms.size() > maxAnotherUserLikedFilms.size()) {
                    maxAnotherUserLikedFilms = anotherUserLikedFilms;
                }
            }
        }
        final Set<Film> recommendedFilms = new LinkedHashSet<>(maxAnotherUserLikedFilms);
        recommendedFilms.removeAll(userLikedFilms);

        return recommendedFilms;
    }
}
