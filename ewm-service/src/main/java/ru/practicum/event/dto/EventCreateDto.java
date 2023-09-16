package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.event.entity.Location;
import ru.practicum.event.enums.EventState;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateDto {
    private Long id;

    @NotNull
    @Size(max = 120, min = 3)
    private String title;

    @NotBlank
    @Size(max = 2000, min = 20)
    private String annotation;

    @NotNull
    private Integer category;

    @NotBlank
    @Size(max = 7000, min = 20)
    private String description;

    @NotNull
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    private EventState eventState;
}
