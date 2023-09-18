package ru.practicum.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.UserInputDto;
import ru.practicum.users.dto.UserFullDto;
import ru.practicum.users.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserFullDto createUser(@RequestBody @Valid UserInputDto userInputDto) {
        return userService.createUser(userInputDto);
    }

    @GetMapping
    public List<UserFullDto> getUser(@RequestParam(required = false, name = "ids") List<Long> userIds,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        return userService.getUsers(userIds, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        log.info("Received a request. Delete user with id={}", userId);
        userService.deleteUser(userId);
    }
}
