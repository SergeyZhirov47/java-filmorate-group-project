package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MPARatingController {
    private final MPAService mpaService;

    @GetMapping
    public List<MPA> getAll() {
        log.info("GET /mpa");
        return mpaService.getAllMPARatings();
    }

    @GetMapping("/{id}")
    public MPA getMPARatingById(@PathVariable(name = "id") int id) {
        log.info(String.format("GET /mpa/{id}, {id} = %s", id));
        return mpaService.getMPARating(id);
    }
}
