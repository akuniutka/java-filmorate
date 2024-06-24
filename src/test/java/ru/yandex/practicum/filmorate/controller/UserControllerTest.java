package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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
import static ru.yandex.practicum.filmorate.TestModels.getRandomUser;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String URL = "/users";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(ignoreStubs(userService));
    }

    @Test
    public void shouldRespondOkAndReturnUserWhenPost() throws Exception {
        final User userToSend = getRandomUser();
        final User userToReceive = getRandomUser();
        userToReceive.setId(faker.number().randomNumber());
        when(userService.create(userToSend)).thenReturn(userToReceive);
        final String jsonToSend = objectMapper.writeValueAsString(userToSend);
        final String expectedJson = objectMapper.writeValueAsString(userToReceive);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(userService).create(userToSend);
    }

    @ParameterizedTest
    @EmptySource
    public void shouldRespondBadRequestWhenPostAndNoBody(String bodyToSend) throws Exception {
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldRespondBadRequestWhenPostAndEmailNullOrBlank(String email) throws Exception {
        final User user = getRandomUser();
        user.setEmail(email);
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRespondBadRequestWhenPostAndEmailMalformed() throws Exception {
        final User user = getRandomUser();
        user.setEmail("text");
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldRespondBadRequestWhenPostAndLoginNullOrBlank(String login) throws Exception {
        final User user = getRandomUser();
        user.setLogin(login);
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRespondBadRequestWhenPostAndLoginContainsWhitespace() throws Exception {
        final User user = getRandomUser();
        user.setLogin("super admin");
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullSource
    public void shouldRespondBadRequestWhenPostAndBirthdayNull(LocalDate birthday) throws Exception {
        final User user = getRandomUser();
        user.setBirthday(birthday);
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRespondBadRequestWhenPostAndBirthdayFuture() throws Exception {
        final User user = getRandomUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRespondOkAndReturnUsersWhenGet() throws Exception {
        final User userA = getRandomUser();
        userA.setId(faker.number().randomNumber());
        final User userB = getRandomUser();
        userB.setId(faker.number().randomNumber());
        final Collection<User> users = List.of(userA, userB);
        when(userService.findAll()).thenReturn(users);
        final String expectedJson = objectMapper.writeValueAsString(users);

        mvc.perform(get(URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(userService).findAll();
    }

    @ParameterizedTest
    @EmptySource
    public void shouldRespondOkAndReturnEmptyListWhenGetAndEmptyList(Collection<User> users) throws Exception {
        when(userService.findAll()).thenReturn(users);
        final String expectedJson = objectMapper.writeValueAsString(users);

        mvc.perform(get(URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(userService).findAll();
    }

    @Test
    public void shouldRespondOkAndReturnUserWhenPut() throws Exception {
        final User userToSend = getRandomUser();
        userToSend.setId(faker.number().randomNumber());
        final User userToReceive = getRandomUser();
        userToReceive.setId(faker.number().randomNumber());
        when(userService.update(userToSend)).thenReturn(userToReceive);
        final String jsonToSend = objectMapper.writeValueAsString(userToSend);
        final String expectedJson = objectMapper.writeValueAsString(userToReceive);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson, true));
        verify(userService).update(userToSend);
    }

    @ParameterizedTest
    @EmptySource
    public void shouldRespondBadRequestWhenPutAndNoBody(String bodyToSend) throws Exception {
        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRespondNotFoundWhenPutAndUserNotFound() throws Exception {
        final User user = getRandomUser();
        user.setId(faker.number().randomNumber());
        when(userService.update(user)).thenThrow(new NotFoundException("User not found"));
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(userService).update(user);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldRespondBadRequestWhenPutAndEmailNullOrBlank(String email) throws Exception {
        final User user = getRandomUser();
        user.setId(faker.number().randomNumber());
        user.setEmail(email);
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRespondBadRequestWhenPutAndEmailMalformed() throws Exception {
        final User user = getRandomUser();
        user.setId(faker.number().randomNumber());
        user.setEmail("text");
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void shouldRespondBadRequestWhenPutAndLoginNullOrBlank(String login) throws Exception {
        final User user = getRandomUser();
        user.setId(faker.number().randomNumber());
        user.setLogin(login);
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRespondBadRequestWhenPutAndLoginContainsWhitespace() throws Exception {
        final User user = getRandomUser();
        user.setId(faker.number().randomNumber());
        user.setLogin("super admin");
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullSource
    public void shouldRespondBadRequestWhenPutAndBirthdayNull(LocalDate birthday) throws Exception {
        final User user = getRandomUser();
        user.setId(faker.number().randomNumber());
        user.setBirthday(birthday);
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRespondBadRequestWhenPutAndBirthdayFuture() throws Exception {
        final User user = getRandomUser();
        user.setId(faker.number().randomNumber());
        user.setBirthday(LocalDate.now().plusDays(1));
        final String jsonToSend = objectMapper.writeValueAsString(user);

        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToSend)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}