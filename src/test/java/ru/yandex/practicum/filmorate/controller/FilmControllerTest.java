package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.TestModels.faker;
import static ru.yandex.practicum.filmorate.TestModels.getRandomFilm;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    private static final String URL = "/films";
    private static final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FilmService filmService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(ignoreStubs(filmService));
    }

    @Test
    void shouldRespondOkAndReturnFilmWhenPost() throws Exception {
        final Film filmToSend = getRandomFilm();
        final Film filmToReceive = getRandomFilm();
        filmToReceive.setId(faker.number().randomNumber());
        when(filmService.create(filmToSend)).thenReturn(filmToReceive);
        final String jsonToSend = objectMapper.writeValueAsString(filmToSend);
        final String expectedJson = objectMapper.writeValueAsString(filmToReceive);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(filmService).create(filmToSend);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldRespondOkAndReturnFilmWhenPostAndDescriptionNullOrBlank(final String description) throws Exception {
        final Film filmToSend = getRandomFilm();
        filmToSend.setDescription(description);
        final Film filmToReceive = getRandomFilm();
        filmToReceive.setId(faker.number().randomNumber());
        when(filmService.create(filmToSend)).thenReturn(filmToReceive);
        final String jsonToSend = objectMapper.writeValueAsString(filmToSend);
        final String expectedJson = objectMapper.writeValueAsString(filmToReceive);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(filmService).create(filmToSend);
    }

    @Test
    void shouldRespondOkAndReturnFilmWhenPostAndReleaseDateEqualsCinemaBirthday() throws Exception {
        final Film filmToSend = getRandomFilm();
        filmToSend.setReleaseDate(cinemaBirthday);
        final Film filmToReceive = getRandomFilm();
        filmToReceive.setId(faker.number().randomNumber());
        when(filmService.create(filmToSend)).thenReturn(filmToReceive);
        final String jsonToSend = objectMapper.writeValueAsString(filmToSend);
        final String expectedJson = objectMapper.writeValueAsString(filmToReceive);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(filmService).create(filmToSend);
    }

    @Test
    void shouldRespondOkAndReturnFilmWhenPostAndDuration1Minute() throws Exception {
        final Film filmToSend = getRandomFilm();
        filmToSend.setDuration(1);
        final Film filmToReceive = getRandomFilm();
        filmToReceive.setId(faker.number().randomNumber());
        when(filmService.create(filmToSend)).thenReturn(filmToReceive);
        final String jsonToSend = objectMapper.writeValueAsString(filmToSend);
        final String expectedJson = objectMapper.writeValueAsString(filmToReceive);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(filmService).create(filmToSend);
    }

    @ParameterizedTest
    @EmptySource
    void shouldRespondBadRequestWhenPostAndNoBody(final String bodyToSend) throws Exception {
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldRespondBadRequestWhenPostAndNameNullOrBlank(final String name) throws Exception {
        final Film film = getRandomFilm();
        film.setName(name);
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondBadRequestWhenPostAndDescriptionExceed200Characters() throws Exception {
        final Film film = getRandomFilm();
        film.setDescription(faker.lorem().characters(201, true, true));
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullSource
    void shouldRespondBadRequestWhenPostAndReleaseDateNull(final LocalDate releaseDate) throws Exception {
        final Film film = getRandomFilm();
        film.setReleaseDate(releaseDate);
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondBadRequestWhenPostAndReleaseDateBeforeCinemaBirthday() throws Exception {
        final Film film = getRandomFilm();
        film.setReleaseDate(cinemaBirthday.minusDays(1L));
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0})
    void shouldRespondBadRequestWhenPostAndDurationNullOrZeroOrNegative(final Integer duration) throws Exception {
        final Film film = getRandomFilm();
        film.setDuration(duration);
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondOkAndReturnFilmsWhenGet() throws Exception {
        final Film filmA = getRandomFilm();
        filmA.setId(faker.number().randomNumber());
        final Film filmB = getRandomFilm();
        filmB.setId(faker.number().randomNumber());
        final Collection<Film> films = List.of(filmA, filmB);
        when(filmService.findAll()).thenReturn(films);
        final String expectedJson = objectMapper.writeValueAsString(films);

        mvc.perform(get(URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(filmService).findAll();
    }

    @ParameterizedTest
    @EmptySource
    void shouldRespondOkAndReturnEmptyListWhenGetAndEmptyList(final Collection<Film> films) throws Exception {
        when(filmService.findAll()).thenReturn(films);
        final String expectedJson = objectMapper.writeValueAsString(films);

        mvc.perform(get(URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(filmService).findAll();
    }

    @Test
    void shouldRespondOkAndReturnFilmWhenPut() throws Exception {
        final Film filmToSend = getRandomFilm();
        filmToSend.setId(faker.number().randomNumber());
        final Film filmToReceive = getRandomFilm();
        filmToReceive.setId(faker.number().randomNumber());
        when(filmService.update(filmToSend)).thenReturn(filmToReceive);
        final String jsonToSend = objectMapper.writeValueAsString(filmToSend);
        final String expectedJson = objectMapper.writeValueAsString(filmToReceive);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(filmService).update(filmToSend);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldRespondOkAndReturnFilmWhenPutAndDescriptionNullOrBlank(final String description) throws Exception {
        final Film filmToSend = getRandomFilm();
        filmToSend.setId(faker.number().randomNumber());
        filmToSend.setDescription(description);
        final Film filmToReceive = getRandomFilm();
        filmToReceive.setId(faker.number().randomNumber());
        when(filmService.update(filmToSend)).thenReturn(filmToReceive);
        final String jsonToSend = objectMapper.writeValueAsString(filmToSend);
        final String expectedJson = objectMapper.writeValueAsString(filmToReceive);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(filmService).update(filmToSend);
    }

    @Test
    void shouldRespondOkAndReturnFilmWhenPutAndReleaseDateEqualsCinemaBirthday() throws Exception {
        final Film filmToSend = getRandomFilm();
        filmToSend.setId(faker.number().randomNumber());
        filmToSend.setReleaseDate(cinemaBirthday);
        final Film filmToReceive = getRandomFilm();
        filmToReceive.setId(faker.number().randomNumber());
        when(filmService.update(filmToSend)).thenReturn(filmToReceive);
        final String jsonToSend = objectMapper.writeValueAsString(filmToSend);
        final String expectedJson = objectMapper.writeValueAsString(filmToReceive);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(filmService).update(filmToSend);
    }

    @Test
    void shouldRespondOkAndReturnFilmWhenPutAndDuration1Minute() throws Exception {
        final Film filmToSend = getRandomFilm();
        filmToSend.setId(faker.number().randomNumber());
        filmToSend.setDuration(1);
        final Film filmToReceive = getRandomFilm();
        filmToReceive.setId(faker.number().randomNumber());
        when(filmService.update(filmToSend)).thenReturn(filmToReceive);
        final String jsonToSend = objectMapper.writeValueAsString(filmToSend);
        final String expectedJson = objectMapper.writeValueAsString(filmToReceive);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(filmService).update(filmToSend);
    }

    @ParameterizedTest
    @EmptySource
    void shouldRespondBadRequestWhenPutAndNoBody(final String bodyToSend) throws Exception {
        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(bodyToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondNotFoundWhenPutAndFilmNotFound() throws Exception {
        final Film film = getRandomFilm();
        final Long filmId = faker.number().randomNumber();
        film.setId(filmId);
        when(filmService.update(film)).thenThrow(new NotFoundException("film", filmId));
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(filmService).update(film);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void shouldRespondBadRequestWhenPutAndNameNullOrBlank(final String name) throws Exception {
        final Film film = getRandomFilm();
        film.setId(faker.number().randomNumber());
        film.setName(name);
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondBadRequestWhenPutAndDescriptionExceed200Characters() throws Exception {
        final Film film = getRandomFilm();
        film.setId(faker.number().randomNumber());
        film.setDescription(faker.lorem().characters(201, true, true));
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullSource
    void shouldRespondBadRequestWhenPutAndReleaseDateNull(final LocalDate releaseDate) throws Exception {
        final Film film = getRandomFilm();
        film.setId(faker.number().randomNumber());
        film.setReleaseDate(releaseDate);
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRespondBadRequestWhenPutAndReleaseDateBeforeCinemaBirthday() throws Exception {
        final Film film = getRandomFilm();
        film.setId(faker.number().randomNumber());
        film.setReleaseDate(cinemaBirthday.minusDays(1L));
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {-1, 0})
    void shouldRespondBadRequestWhenPutAndDurationNullOrZeroOrNegative(final Integer duration) throws Exception {
        final Film film = getRandomFilm();
        film.setId(faker.number().randomNumber());
        film.setDuration(duration);
        final String jsonToSend = objectMapper.writeValueAsString(film);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
