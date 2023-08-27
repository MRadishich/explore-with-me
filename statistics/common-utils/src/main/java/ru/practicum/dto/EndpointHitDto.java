package ru.practicum.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
public class EndpointHitDto {

    private final Long id;

    @NotBlank
    private final String app;

    @NotBlank
    private final String uri;

    @NotBlank
    private final String ip;

    @PastOrPresent
    @NotNull
    private final LocalDateTime timestamp;
}
