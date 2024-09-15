package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private final ManyToManyRelation<User> friends;
    private final ManyToManyRelation<Film> likes;

    @Autowired
    public UserDbStorage(final NamedParameterJdbcTemplate jdbc) {
        super(User.class, jdbc);
        this.friends = new ManyToManyRelation<>(User.class)
                .withJoinTable("friends")
                .withJoinedColumn("friend_id");
        this.likes = new ManyToManyRelation<>(Film.class)
                .withJoinTable("film_likes")
                .withPayloadColumn("mark");
    }

    @Override
    public User save(final User user) {
        return save(List.of("email", "login", "name", "birthday"), user);
    }

    @Override
    public Optional<User> update(final User user) {
        return update(List.of("email", "login", "name", "birthday"), user);
    }

    @Override
    public void addFriend(final long id, final long friendId) {
        friends.addRelation(id, friendId);
    }

    @Override
    public Set<User> findFriends(final long id) {
        return friends.fetchRelations(id);
    }

    @Override
    public boolean deleteFriend(final long id, final long friendId) {
        return friends.dropRelation(id, friendId);
    }

    @Override
    public Map<Film, Integer> findLikes(final long id) {
        final Map<Film, Integer> userLikes = new LinkedHashMap<>();
        likes.fetchRelationsWithPayload(id).forEach((film, like) -> userLikes.put(film, (Integer) like));
        return userLikes;
    }
}
