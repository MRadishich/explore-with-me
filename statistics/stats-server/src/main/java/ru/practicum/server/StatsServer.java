package ru.practicum.server;

import ru.practicum.dto.ViewStatsDto;
import ru.practicum.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsServer {
    EndpointHitDto save(EndpointHitDto endpointHit);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique);
}
