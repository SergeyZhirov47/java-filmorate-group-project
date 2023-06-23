package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;

public interface DirectorStorage {

    List<Director> getAllDirectors();

    Director createDirector(Director director);

    Director getDirector(SqlRowSet directorRow);

    Map<String, Object> directorToMap(Director director);
}
