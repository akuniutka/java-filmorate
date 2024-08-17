package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.NewUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Mapper
public interface UserMapper {

    User mapToUser(NewUserDto dto);

    User mapToUser(UpdateUserDto dto);

    UserDto mapToDto(User user);

    Collection<UserDto> mapToDto(Collection<User> users);
}
