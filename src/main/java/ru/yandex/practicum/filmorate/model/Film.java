package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.MinDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание не должно быть больше 200 символов!")
    private String description;
    @MinDate(minDate = "1895-12-28", message = "Дата релиза фильма не может быть раньше 28 декабря 1895")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть больше нуля")
    private int duration;
}
