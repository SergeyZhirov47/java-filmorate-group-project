package ru.yandex.practicum.filmorate.common;

import lombok.Getter;

public enum ErrorMessage {
    NO_USER("Нет пользователя"),
    NO_FRIEND("Нет пользователя (друга)"),
    NO_FILM("Нет фильма"),
    UPDATE_FAIL("Обновление не успешно"),
    DELETE_FAIL("Удаление не успешно");
    @Getter
    private final String message;
    ErrorMessage(final String message) {
        this.message = message;
    }
}
