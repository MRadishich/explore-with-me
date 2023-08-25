package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class StatsClient extends BaseClient {

    public StatsClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> saveHit(EndpointHitDto hitDto) {
        log.info("Send post request. EndpointHit: {}", hitDto);

        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        String path = "/stats/?start={start}&end={end}&unique={unique}";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", URLEncoder.encode(start.toString(), Charset.defaultCharset()));
        parameters.put("end", URLEncoder.encode(end.toString(), Charset.defaultCharset()));
        parameters.put("unique", unique);

        if (uris != null) {
            parameters.put("uris", String.join(",", uris));
            return get(path + "&uris={uris}", parameters);
        }

        log.info("Send get request. Path={}", path);
        return get(path, parameters);
    }
}
