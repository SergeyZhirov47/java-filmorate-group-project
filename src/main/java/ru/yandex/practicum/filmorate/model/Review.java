package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@Jacksonized
public class Review {
    private int id;
    private int filmId; // id фильма, к которому относится этот отзыв.
    private int userId; // id пользователя, который оставил этот отзыв.
    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String content; // текст отзыва.
    private boolean isPositive; // тип отзыва (положительный/отрицательный).

    // ToDo
    // Т.е отдельно еще будет храниться оценки (можно назвать их лайками) отзывов.
    // Тогда тут будет:
    // 1. Список с этими оценками и будет отдельный метод, который будет рассчитывать числовое значение рейтинга.
    // 2. Или просто числовое значение, а расчет на стороне SQL.

    /*
    Рейтинг отзыва.
        У отзыва имеется рейтинг. При создании отзыва рейтинг равен нулю.
        Если пользователь оценил отзыв как полезный, это увеличивает его рейтинг на 1.
        Если как бесполезный, то уменьшает на 1.
    */
    @JsonProperty("useful")
    private int rating; // рейтинг отзыва.
}
