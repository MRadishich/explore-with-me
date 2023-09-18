package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.users.dto.UserInputDto;
import ru.practicum.users.dto.UserFullDto;
import ru.practicum.users.entity.User;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserFullDto createUser(UserInputDto userInputDto) {
        User user = UserMapper.toUser(userInputDto);
        user = userRepository.save(user);

        return UserMapper.toFullDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserFullDto> getUsers(List<Long> userIds, int from, int size) {
        if (userIds == null) {
            return userRepository.findAll(PageRequest.of(from, size))
                    .stream()
                    .map(UserMapper::toFullDto)
                    .collect(Collectors.toList());
        }

        return userRepository.findByIdIn(userIds, PageRequest.of(from, size))
                .stream()
                .map(UserMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found.");
        }

        userRepository.deleteById(userId);
    }
}
