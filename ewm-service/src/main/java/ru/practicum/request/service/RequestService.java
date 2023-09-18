package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Long usersId, Long eventId);

    List<RequestDto> findAllByUserId(Long usersId);

    RequestDto cancelRequest(Long usersId, Long requestId);
}
