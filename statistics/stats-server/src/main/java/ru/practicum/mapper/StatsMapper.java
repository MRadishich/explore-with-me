package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.entity.EndpointHit;

@UtilityClass
public class StatsMapper {

    public static EndpointHit toEntity(EndpointHitDto hitDto) {
        return new EndpointHit(
                hitDto.getId(),
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp()
        );
    }

    public static EndpointHitDto toDto(EndpointHit hit) {
        return new EndpointHitDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }
}
