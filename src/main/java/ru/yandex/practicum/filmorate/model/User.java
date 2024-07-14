package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {

    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Pattern(regexp = "\\S+", message = "should contain no whitespaces")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @JsonIgnore
    private Set<Long> friends;

    @JsonIgnore
    private Set<Long> likedFilms;
}
