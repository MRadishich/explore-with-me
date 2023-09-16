package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.event.entity.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.constant.Constant.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {
    private final StatsClient statsClient;

    public long getViews(Event event) {
        log.info("get views event id = {}", event.getId());

        String[] uris = {"/events/" + event.getId()};

        List<ViewStatsDto> views = statsClient.getStats(
                event.getCreatedOn().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER)),
                uris,
                true
        );

        if (!views.isEmpty()) {
            return views.get(0).getHits();
        } else {
            return 0;
        }
    }

    public void sendHit(String app, String uri, String ip) {
        log.info("save hit app = {}, uri = {}, ip = {}", app, uri, ip);

        RequestHitDto requestHitDto = new RequestHitDto(
                null,
                app,
                uri,
                ip,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER))
        );

        statsClient.saveHit(requestHitDto);
    }
}
