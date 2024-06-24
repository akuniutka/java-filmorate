package ru.yandex.practicum.filmorate;

import com.github.javafaker.Faker;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public final class TestModels {

    public static final Faker faker = new Faker();

    private TestModels() {
    }

    public static User getRandomUser() {
        final User user = new User();
        user.setEmail(faker.internet().emailAddress());
        user.setLogin(faker.name().username());
        user.setName(faker.name().fullName());
        user.setBirthday(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        return user;
    }

    public static Film getRandomFilm() {
        final Film film = new Film();
        film.setName(faker.book().title());
        film.setDescription(faker.lorem().characters(200, true, true));
        film.setReleaseDate(faker.date().between(
                Date.from(LocalDate.of(1895, 12, 28).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        ).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        film.setDuration(faker.number().numberBetween(1, Integer.MAX_VALUE));
        return film;
    }
}
