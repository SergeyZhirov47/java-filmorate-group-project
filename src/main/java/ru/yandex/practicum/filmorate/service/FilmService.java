package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    protected FilmStorage filmStorage;
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void add(Film film) {
        filmStorage.add(film);
    }

    public void update(Film film) {
        filmStorage.update(film);
    }

    public Film getById(int id) {
        final Optional<Film> filmOpt = filmStorage.get(id);

        if (filmOpt.isEmpty()) {
            throw new NotFoundException(String.format("Нет фильма с id = %s", id));
        }

        return filmOpt.get();
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }
}
