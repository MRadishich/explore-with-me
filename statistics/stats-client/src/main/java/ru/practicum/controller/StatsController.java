package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;

import javax.validation.Valid;
import java.time.LocalDateTime;


@Controller
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> saveHit(@RequestBody @Valid EndpointHitDto hitDto) {
        log.info("Request: save hit. EndpointHit: {}", hitDto);

        return new ResponseEntity<>(statsClient.saveHit(hitDto).getStatusCode());
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) String[] uris,
            @RequestParam(defaultValue = "false") boolean unique
    ) {
        log.info("Request: get statistic. Param: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return statsClient.getStats(start, end, uris, unique);
    }
}
