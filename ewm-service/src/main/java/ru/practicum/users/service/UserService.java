package ru.practicum.users.service;

import ru.practicum.users.dto.UserInputDto;
import ru.practicum.users.dto.UserFullDto;

import java.util.List;

public interface UserService {
    UserFullDto createUser(UserInputDto userInputDto);

    List<UserFullDto> getUsers(List<Long> userIds, int from, int size);

    void deleteUser(long userId);
}
