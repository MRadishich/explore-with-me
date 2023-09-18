package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.entity.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.constant.Constant.DATE_TIME_FORMATTER;

@UtilityClass
public class StatsMapper {

    public static EndpointHit toEntity(RequestHitDto hitDto) {
        return new EndpointHit(
                hitDto.getId(),
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                LocalDateTime.parse(hitDto.getTimestamp(), DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER))
        );
    }

    public static HitDto toDto(EndpointHit hit) {
        return new HitDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp()
        );
    }
}
