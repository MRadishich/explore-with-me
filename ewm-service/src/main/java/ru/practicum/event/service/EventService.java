package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventState;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestResultDto;
import ru.practicum.request.dto.RequestUpdateDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(EventCreateDto eventCreateDto, Long userId);

    List<EventShortDto> getEventsByUserId(Long userId, int from, int size);

    EventFullDto getEventById(Long eventId, Long userId);

    EventFullDto updateByOwner(EventUpdateUserDto eventUpdateUserDto, Long eventId, Long userId);

    List<RequestDto> getUserRequests(Long userId, Long requestId);

    RequestResultDto updateRequestStatus(RequestUpdateDto requestUpdateDto, Long userId, Long eventId);

    List<EventFullDto> getAllByParamForAdmin(
            List<Long> users,
            List<EventState> states,
            List<Integer> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size);

    EventFullDto adminUpdate(EventUpdateAdminDto eventUpdateAdminDto, Long eventId);

    List<EventFullDto> getAllByParamForPublic(int from,
                                              int size,
                                              String text,
                                              List<Integer> categories,
                                              Boolean paid,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Boolean onlyAvailable,
                                              String sort);

    EventFullDto getEventById(Long id);
}
