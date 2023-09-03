package ru.practicum.users.mapper;

import ru.practicum.users.dto.UserInputDto;
import ru.practicum.users.dto.UserOutputDto;
import ru.practicum.users.entity.User;

public class UserMapper {
    public static UserOutputDto toDto(User user) {
        return new UserOutputDto(
                user.getId(),
                user.getName(),
                user.getEmail()
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
