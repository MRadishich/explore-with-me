package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
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
