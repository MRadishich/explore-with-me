package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventCreateDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.entity.Event;
import ru.practicum.event.enums.EventState;
import ru.practicum.users.mapper.UserMapper;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public static EventFullDto toFullDto(Event event, Integer confirmedRequests) {
        return new EventFullDto(
                event.getId(),
                event.getTitle(),
                event.getAnnotation(),
                event.getCategory(),
                confirmedRequests,
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                UserMapper.toShortDto(event.getInitiator()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                null
        );
    }

    public static EventShortDto toShortDto(Event event, Integer confirmedRequests) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                event.getTitle(),
                event.getCategory(),
                confirmedRequests,
                event.getDescription(),
                event.getEventDate(),
                UserMapper.toShortDto(event.getInitiator()),
                event.getPaid(),
                null
        );
    }

    public static Event toEvent(EventCreateDto eventShortDto) {
        return Event.builder()
                .id(eventShortDto.getId())
                .annotation(eventShortDto.getAnnotation())
                .title(eventShortDto.getTitle())
                .description(eventShortDto.getDescription())
                .eventDate(eventShortDto.getEventDate())
                .location(eventShortDto.getLocation())
                .paid(eventShortDto.getPaid())
                .participantLimit(eventShortDto.getParticipantLimit() == null ? 0L : eventShortDto.getParticipantLimit())
                .requestModeration(eventShortDto.getRequestModeration() == null || eventShortDto.getRequestModeration())
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .build();
    }
}
