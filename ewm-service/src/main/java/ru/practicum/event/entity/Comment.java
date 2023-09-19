package ru.practicum.event.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created")
    private LocalDateTime createDate;

    @Column(name = "updated")
    private LocalDateTime updateDate;

    private String text;
}
