package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestResultDto;
import ru.practicum.request.dto.RequestUpdateDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Valid EventCreateDto eventCreateDto,
                                    @PathVariable long userId) {
        return eventService.createEvent(eventCreateDto, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findAllByUserId(@PathVariable long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return eventService.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findById(@PathVariable long userId,
                                 @PathVariable long eventId) {
        return eventService.getEventById(eventId, userId);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> findRequests(@PathVariable long userId,
                                         @PathVariable long eventId) {
        return eventService.getUserRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@RequestBody @Valid EventUpdateUserDto eventUpdateUserDto,
                               @PathVariable long userId,
                               @PathVariable long eventId) {
        return eventService.updateByOwner(eventUpdateUserDto, eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestResultDto updateRequests(@RequestBody @Valid RequestUpdateDto requestUpdateDto,
                                           @PathVariable long userId,
                                           @PathVariable long eventId) {
        return eventService.updateRequestStatus(requestUpdateDto, userId, eventId);
    }

    @PostMapping("/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentFullDto createComment(@RequestBody @Valid CommentInputDto commentInputDto,
                                        @PathVariable long userId,
                                        @PathVariable long eventId) {
        return eventService.createComment(commentInputDto, userId, eventId);
    }

    @PatchMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto updateComment(@RequestBody @Valid CommentInputDto commentInputDto,
                                        @PathVariable long userId,
                                        @PathVariable long commentId) {
        return eventService.updateComment(commentInputDto, commentId, userId);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long userId,
                              @PathVariable long commentId) {
        eventService.deleteCommentPrivate(commentId, userId);
    }
}
