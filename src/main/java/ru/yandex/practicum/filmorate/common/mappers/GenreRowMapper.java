package ru.yandex.practicum.filmorate.common.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreRowMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        final Genre genre = Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();

        return genre;
    }
}
