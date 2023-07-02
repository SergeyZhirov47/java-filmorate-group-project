package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.parameters.FilmSortParameters;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film add(@Valid @RequestBody Film newFilm) {
        log.info("POST /films");
        final int filmId = filmService.add(newFilm);
        log.info(String.format("Добавлен новый фильм %s", newFilm.getName()));

        return filmService.getById(filmId);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT /films");
        filmService.update(film);
        log.info(String.format("Обновлена информация о фильме с id %s", film.getId()));

        return filmService.getById(film.getId());
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("GET /films");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable(name = "id") int id) {
        log.info(String.format("GET /films/{id}, {id} = %s", id));
        return filmService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info(String.format("PUT /films/{id}/like/{userId}, {id} = %s, {userId} = %s", id, userId));
        filmService.addLike(id, userId);
        log.info(String.format("Пользователь с id = %s поставил лайк фильму с id = %s", userId, id));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        log.info(String.format("DELETE /films/{id}/like/{userId}, {id} = %s, {userId} = %s", id, userId));
        filmService.deleteLike(id, userId);
        log.info(String.format("Пользователь с id = %s удалил свой лайк фильму с id = %s", userId, id));
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(name = "count", defaultValue = "10") Integer count,
                                 @RequestParam(value = "genreId", required = false) Integer genreId,
                                 @RequestParam(value = "year", required = false) Integer year) {
        log.info(String.format("GET /films/popular?count={count}&genreId={genreId}&year={year}, {count} = %s, " +
                "{genreID} = %s, {year} = %s", count, genreId, year));
        return filmService.getPopularByGenresAndYear(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilmByDirector(@RequestParam(name = "sortBy") FilmSortParameters param,
                                              @PathVariable int directorId) {
        log.info(String.format("GET /films/director/directorId={directorId}?sortBy={param}, "
                + "{directorId} = %s, {param} = %s", directorId, param));
        List<Film> films = filmService.getSortedFilmByDirector(param, directorId);
        log.info(String.format("Список фильмов режиссера с id = {id}, отсортированных по параметру = {param} получен, "
                + "{id} = %d, {param} = %s", directorId, param));
        return films;
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable(name = "id") int id) {
        log.info(String.format("DELETE /films/{id}, {id} = %s", id));
        filmService.deleteFilmById(id);
        log.info(String.format("Фильм с id = %s успешно удален", id));
    }

    @GetMapping("/search")
    public List<Film> search(@RequestParam String query, @RequestParam String by) {
        log.info(String.format("GET /films/search?query={query}&by={by}, {query} = %s " + "{by} = %s", query, by));
        List<Film> films = filmService.search(query, by);
        log.info(String.format("Результаты поиска по \"%s\" получены", query));
        return films;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(name = "userId") int userId,
                                     @RequestParam(name = "friendId") int friendId) {
        log.info(String.format(
                "Поступил запрос на получение списка общих фильмов пользователя с id %s и %s", userId, friendId));
        return filmService.getCommonFilms(userId, friendId);
    }
}
