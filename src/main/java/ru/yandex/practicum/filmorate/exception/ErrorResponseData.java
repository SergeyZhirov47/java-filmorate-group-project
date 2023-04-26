package ru.yandex.practicum.filmorate.exception;

import lombok.Data;

@Data
public class ErrorResponseData {
    private final String message;
}
