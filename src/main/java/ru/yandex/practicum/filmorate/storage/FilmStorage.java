package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> get(int id);

    int add(Film film);

    void update(Film film);

    void delete(Film film);

    void deleteById(int id);

    List<Film> getAll();

    boolean contains(int id);

    Optional<Genre> getGenreById(int id);

    List<Genre> getAllGenres();

    Optional<MPA> getMPARatingById(int id);

    List<MPA> getAllMPARatings();
}
