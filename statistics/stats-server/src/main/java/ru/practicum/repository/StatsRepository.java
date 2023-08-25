package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.entity.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT h.app as app, h.uri as uri, COUNT(h.id) as hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<ViewStatsDto> findAllHits(LocalDateTime start, LocalDateTime end);

    @Query("SELECT h.app as app, h.uri as uri, COUNT(h.id) as hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<ViewStatsDto> findAllHitsByUris(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query("SELECT h.app as app, h.uri as uri, COUNT(DISTINCT h.ip) as hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<ViewStatsDto> findUniqueHits(LocalDateTime start, LocalDateTime end);

    @Query("SELECT h.app as app, h.uri as uri, COUNT(DISTINCT h.ip) as hits " +
            "FROM EndpointHit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY hits DESC")
    List<ViewStatsDto> findUniqueHitsByUris(LocalDateTime start, LocalDateTime end, String[] uris);
}
