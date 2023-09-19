package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentFullDto {
    private Long id;
    private Long eventId;
    private Long userId;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String text;
}
