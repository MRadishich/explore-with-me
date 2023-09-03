package ru.practicum.users.service;

import ru.practicum.users.dto.UserInputDto;
import ru.practicum.users.dto.UserOutputDto;

import java.util.List;

public interface UserService {
    UserOutputDto createUser(UserInputDto userInputDto);

    List<UserOutputDto> getUsers(List<Long> userIds, int from, int size);

    void deleteUser(long userId);
}
