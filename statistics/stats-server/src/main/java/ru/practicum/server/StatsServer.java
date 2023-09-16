package ru.practicum.server;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.RequestHitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServer {
    HitDto save(RequestHitDto endpointHit);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
