package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
   private final FilmService filmService;

    @Autowired
   public FilmController(final FilmService filmService) {
       this.filmService = filmService;
   }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film add(@Valid @RequestBody Film newFilm) {
        log.info("POST /films");
        filmService.add(newFilm);
        log.info(String.format("Добавлен новый фильм %s", newFilm.getName()));

        return newFilm;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT /films");
        filmService.update(film);
        log.info(String.format("Обновлена информация о фильме с id %s", film.getId()));

        return film;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAll() {
        log.info("GET /films");
        return filmService.getAll();
    }

    // GET .../films/{id}
    @GetMapping("/films/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable int id) {
        log.info("GET /films/{id}");
        return filmService.getById(id);
    }

    /*
    PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков. Если значение параметра count не задано, верните первые 10.
     */

    @PutMapping("/films/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    // ToDo
    // Здесь задавать кол-во по умолчанию (если параметр не задан?) или в сервисе? (наверное в сервисе)
    @GetMapping("/films/popular?count={count}")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getPopular(@RequestParam Optional<Integer> count) {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
