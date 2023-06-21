package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private static final String SELECT_DIRECTOR = "SELECT * FROM directors";
    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;
    private final DirectorRowMapper directorRowMapper = new DirectorRowMapper();

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query(SELECT_DIRECTOR + ";", directorRowMapper);
    }

    @Override
    public Director createDirector(Director director) {
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        director.setId(simpleJdbcInsert
                .executeAndReturnKey(this.directorToMap(director)).intValue());
        return director;
    }

    @Override
    public Director getDirectorById(int id) {
        final SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(
                SELECT_DIRECTOR + " WHERE director_id = ?;", id);
        Director director;
        if (!sqlRowSet.next()) {
            throw new NotFoundException("Фильм с таким ID не найден");
        }
        director = getDirector(sqlRowSet);
        return director;
    }

    @Override
    public Director update(Director director) {
        String sqlQuery = "UPDATE directors SET name = ? WHERE director_id = ?;";
        if (jdbcTemplate.update(sqlQuery, director.getName(), director.getId()) != 1) {
            throw new NotFoundException("Режиссер с таким ID не найден");
        }
        return getDirectorById(director.getId());
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "delete from directors where director_id = ?";
        if (jdbcTemplate.update(sqlQuery, id) != 1) {
            throw new NotFoundException("Режиссер с таким ID не найден");
        }
    }

    private Director getDirector(SqlRowSet directorRow) {
        return new Director(directorRow.getInt("director_id"),
                directorRow.getString("name"));
    }

    private Map<String, Object> directorToMap(Director director) {
        final Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        return values;
    }
}
