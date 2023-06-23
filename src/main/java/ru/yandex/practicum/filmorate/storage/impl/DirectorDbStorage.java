package ru.yandex.practicum.filmorate.storage.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private static final String SELECT_DIRECTOR = "SELECT * FROM directors";
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final DirectorRowMapper directorRowMapper = new DirectorRowMapper();

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query(SELECT_DIRECTOR + ";", directorRowMapper);
    }

    @Override
    public Director createDirector(Director director) {
        director.setId(simpleJdbcInsert
                .executeAndReturnKey(this.directorToMap(director)).intValue());
        return director;
    }

    @Override
    public Director getDirector(SqlRowSet directorRow) {
        return new Director(directorRow.getInt("director_id"),
                directorRow.getString("name"));
    }

    @Override
    public Map<String, Object> directorToMap(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        return values;
    }
}
