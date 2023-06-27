package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface DirectorStorage {

    List<Director> getAllDirectors();

    Director createDirector(Director director);

    Director getDirectorById(int id);

    Director update(Director director);

    void delete(int id);

    List<Director> getDirectorsByIds(List<Integer> idList);
}
