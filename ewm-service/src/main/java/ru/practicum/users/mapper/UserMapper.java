package ru.practicum.users.mapper;

import ru.practicum.users.dto.UserInputDto;
import ru.practicum.users.dto.UserFullDto;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.entity.User;

public class UserMapper {
    public static UserFullDto toFullDto(User user) {
        return new UserFullDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserShortDto toShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }


    public static User toUser(UserInputDto userInputDto) {
        return new User(
                null,
                userInputDto.getName(),
                userInputDto.getEmail()
        );
    }
}
