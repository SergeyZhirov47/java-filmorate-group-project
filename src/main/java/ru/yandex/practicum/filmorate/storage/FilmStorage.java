package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> get(int id);
    void add(Film film);
    void update(Film film);
    void delete(Film film);
    void deleteById(int id);
    List<Film> getAll();
}
