package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> get(int id);

    List<Film> get(List<Integer> idList);

    int add(Film film);

    void update(Film film);

    void delete(Film film);

    void deleteById(int id);

    List<Film> getAll();

    boolean contains(int id);

    List<Film> getPopular(Optional<Integer> count);

    List<Film> getPopularByGenresAndYear(Optional<Integer> count, Optional<Integer> genreId, Optional<Integer> year);

    List<Film> getSortedFilmByDirector(String param, int directorId);

    List<Film> search(String query, String by);
}