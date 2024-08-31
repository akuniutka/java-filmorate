package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.AbstractEventStorageTest;
import ru.yandex.practicum.filmorate.storage.api.UserStorage;

import static ru.yandex.practicum.filmorate.TestModels.getRandomUser;

@JdbcTest
public class EventDbStorageIT extends AbstractEventStorageTest {

    private final UserStorage userStorage;

    @Autowired
    EventDbStorageIT(final NamedParameterJdbcTemplate jdbc) {
        this.eventStorage = new EventDbStorage(jdbc);
        this.userStorage = new UserDbStorage(jdbc);
    }

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 2; i++) {
            User user = getRandomUser();
            User savedUser = userStorage.save(user);
            userIds[i] = savedUser.getId();
        }
        userIds[2] = userIds[0];
    }
}
