package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private static final String SELECT_DIRECTOR = "SELECT * FROM directors";
    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper directorRowMapper = new DirectorRowMapper();

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query(SELECT_DIRECTOR + ";", directorRowMapper);
    }
}
