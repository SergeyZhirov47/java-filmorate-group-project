package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> getGenreById(int id);

    List<Genre> getGenreById(List<Integer> idList);

    List<Genre> getAllGenres();

}
