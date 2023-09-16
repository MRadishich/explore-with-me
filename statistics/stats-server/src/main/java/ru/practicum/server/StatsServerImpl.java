package ru.practicum.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.entity.EndpointHit;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServerImpl implements StatsServer {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public HitDto save(RequestHitDto hitDto) {
        EndpointHit hit = StatsMapper.toEntity(hitDto);
        hit = statsRepository.save(hit);

        return StatsMapper.toDto(hit);
    }

    @Override
    @Transactional
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        List<ViewStatsDto> viewStats;

        if (!unique && uris == null) {
            log.info("Find all hits. Param: start={}, end={}", start, end);
            viewStats = statsRepository.findAllHits(start, end);
        } else if (unique && uris == null) {
            log.info("Find unique hits. Param: start={}, end={}", start, end);
            viewStats = statsRepository.findUniqueHits(start, end);
        } else if (unique) {
            log.info("Find unique hits by uris. Param: start={}, end={}, uris={}", start, end, uris);
            viewStats = statsRepository.findUniqueHitsByUris(start, end, uris);
        } else {
            log.info("Find all hits by uris. Param: start={}, end={}, uris={}", start, end, uris);
            viewStats = statsRepository.findAllHitsByUris(start, end, uris);
        }

        return viewStats;
    }
}
