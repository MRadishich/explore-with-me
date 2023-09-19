package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.event.dto.CommentFullDto;
import ru.practicum.event.dto.CommentShortDto;
import ru.practicum.event.entity.Comment;

@UtilityClass
public class CommentMapper {
    public static CommentFullDto toFullDto(Comment comment) {
        return new CommentFullDto(
                comment.getId(),
                comment.getEventId(),
                comment.getUserId(),
                comment.getCreateDate(),
                comment.getUpdateDate(),
                comment.getText()
        );
    }

    public static CommentShortDto toShortDto(Comment comment) {
        return new CommentShortDto(
                comment.getId(),
                comment.getUserId(),
                comment.getCreateDate(),
                comment.getText()
        );
    }
}
