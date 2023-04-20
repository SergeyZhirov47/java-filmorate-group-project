package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Film {
    private final int id;
    private String name;
    private String description;
    private Instant releaseDate;
    private int duration;
}
