package ru.yandex.practicum.filmorate.common.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<String> genres = null;

        final String genreColumnValue = rs.getString("genre");
        if (!isNull(genreColumnValue)) {
            genres = Arrays.stream(genreColumnValue.split(",")).map(String::trim).collect(Collectors.toUnmodifiableList());
        }

        final Film film = Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .genres(genres)
                .rating(rs.getString("rating"))
                .build();

        return film;
    }
}