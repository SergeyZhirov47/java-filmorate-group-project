package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.filmorate.validation.MinDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@Jacksonized
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
    private Set<Genre> genres;
    @JsonProperty("mpa")
    private MPA rating;
    private Set<Director> directors;
}
