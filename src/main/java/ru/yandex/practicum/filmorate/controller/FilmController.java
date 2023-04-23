package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int lastId = 0;
    private int nextId() {
        lastId++;
        return lastId;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film add(@Valid @RequestBody Film newFilm) {
        final int newId = nextId();
        newFilm.setId(newId);

        films.put(newId, newFilm);
        log.info(String.format("Добавлен новый фильм %s", newFilm.getName()));

        return newFilm;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Valid @RequestBody Film film) {
        final int filmId = film.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, film);
        } else {
            throw new NotFoundException(String.format("Нет фильма с id = %s. Обновление не успешно.", filmId));
        }

        log.info(String.format("Обновлена информация о фильме с id %s", filmId));

        return film;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAll() {
        return films.values().stream().collect(Collectors.toUnmodifiableList());
    }
}
