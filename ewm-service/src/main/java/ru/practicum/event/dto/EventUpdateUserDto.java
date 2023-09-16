package ru.practicum.event.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.event.entity.Location;
import ru.practicum.event.enums.StateAction;

import javax.validation.constraints.Future;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class EventUpdateUserDto {

    @Size(max = 120, min = 3)
    private String title;

    @Size(max = 2000, min = 20)
    private String annotation;

    @Size(max = 7000, min = 20)
    private String description;

    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Boolean requestModeration;

    private Integer category;

    @PositiveOrZero
    private Long participantLimit;

    private StateAction stateAction;
}
