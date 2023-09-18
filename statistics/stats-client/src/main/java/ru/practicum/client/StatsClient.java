package ru.practicum.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.dto.ViewStatsDto;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
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

    public ResponseEntity<Object> saveHit(@Valid RequestHitDto hitDto) {
        log.info("Send post request. EndpointHit: {}", hitDto);

        return post("/hit", hitDto);
    }

    public List<ViewStatsDto> getStats(String start, String end, String[] uris, boolean unique) {
        log.info("Get stats. start = {}, end = {}.", start, end);

        String path = "/stats/?start={start}&end={end}&unique={unique}";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", URLEncoder.encode(start, Charset.defaultCharset()));
        parameters.put("end", URLEncoder.encode(end, Charset.defaultCharset()));
        parameters.put("unique", unique);

        if (uris != null) {
            parameters.put("uris", String.join(",", uris));
            log.info("Send get request with uris. Base path={}", path);

            ResponseEntity<Object> response = get(path + "&uris={uris}", parameters);

            log.info("Response body = {}", response.getBody());
            return new ObjectMapper().convertValue(response.getBody(), new TypeReference<List<ViewStatsDto>>() {});
        }

        log.info("Send get request. Base path={}", path);

        ResponseEntity<Object> response = get(path, parameters);

        log.info("Response body = {}", response.getBody());
        return new ObjectMapper().convertValue(response.getBody(), new TypeReference<List<ViewStatsDto>>() {
        });
    }
}
