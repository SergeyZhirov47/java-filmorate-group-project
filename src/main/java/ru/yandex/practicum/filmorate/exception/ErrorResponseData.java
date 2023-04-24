package ru.yandex.practicum.filmorate.exception;

import lombok.Data;
import lombok.Getter;

@Data
public class ErrorResponseData {
    @Getter
    private final String message;
}
