package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.StatsServer;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsServer statsServer;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto saveHit(@RequestBody EndpointHitDto hitDto) {
        log.info("Request: save hit. EndpointDto: {}", hitDto);
        return statsServer.save(hitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) String[] uris,
            @RequestParam(defaultValue = "false") boolean unique
    ) {
        LocalDateTime startDate = LocalDateTime.parse(URLDecoder.decode(start, Charset.defaultCharset()));
        LocalDateTime endDate = LocalDateTime.parse(URLDecoder.decode(end, Charset.defaultCharset()));
        log.info("Request: get statistic. Param: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statsServer.getStats(startDate, endDate, uris, unique);
    }
}
