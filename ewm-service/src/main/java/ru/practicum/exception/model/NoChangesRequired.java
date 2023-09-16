package ru.practicum.exception.model;

public class NoChangesRequired extends RuntimeException {
    public NoChangesRequired(String message) {
        super(message);
    }
}
