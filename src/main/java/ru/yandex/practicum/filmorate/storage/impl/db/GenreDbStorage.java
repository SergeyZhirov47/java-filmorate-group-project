package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

@Component
@Qualifier("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final static String SELECT_GENRE = "SELECT id, name FROM \"genres\"";

    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper = new GenreRowMapper();

    @Override
    public Optional<Genre> getGenreById(int id) {
        final String sql = SELECT_GENRE + " WHERE id = ?;";

        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sql, genreRowMapper, id);
        }
        catch (EmptyResultDataAccessException exp) {
            genre = null;
        }

        return Optional.ofNullable(genre);
    }

    @Override
    public List<Genre> getGenreById(List<Integer> idList) {
        final String idString = idList.stream().map(String::valueOf).collect(joining(", "));
        final String sql = SELECT_GENRE + " WHERE id iN (?);";

        final List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, idString);
        return genres;
    }

    @Override
    public List<Genre> getAllGenres() {
        final List<Genre> genres = jdbcTemplate.query(SELECT_GENRE + ";", genreRowMapper);
        return genres;
    }
}
