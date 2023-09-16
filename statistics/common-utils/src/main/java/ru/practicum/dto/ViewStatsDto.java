package ru.practicum.dto;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatsDto {
    private String app;

    private String uri;

    private Long hits;
}
