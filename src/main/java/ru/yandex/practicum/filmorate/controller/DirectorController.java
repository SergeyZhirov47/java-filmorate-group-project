package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> getAllDirectors() {
        log.info("GET /directors");
        final List<Director> directors = directorService.getAllDirectors();
        log.info("Получен список всех режиссеров");
        return directors;
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info(String.format("GET /directors/{id}, {id} = %s", id));
        Director director = directorService.getDirectorById(id);
        log.info(String.format("Получен режиссер с id = %d", id));
        return director;
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("POST /directors");
        Director dir = directorService.createDirector(director);
        log.info(String.format("Режиссер %s добавлен", dir.getName()));
        return dir;
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("PUT /directors");
        Director dir = directorService.update(director);
        log.info(String.format("Режиссер с id = %d обновлен", dir.getId()));
        return dir;
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        log.info(String.format("DELETE /directors/{id}, {id} = %s", id));
        directorService.removeDirector(id);
        log.info(String.format("Режиссер с id = %d удален", id));
    }
}
