package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

@UtilityClass
public class RequestMapper {
    public static RequestDto toDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getEvent().getId(),
                request.getCreated(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }
}
