package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final static String SELECT_GENRE = "SELECT id, name FROM \"genres\"";

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final GenreRowMapper genreRowMapper = new GenreRowMapper();

    @Override
    public Optional<Genre> getGenreById(int id) {
        final String sql = SELECT_GENRE + " WHERE id = ?;";

        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sql, genreRowMapper, id);
        } catch (EmptyResultDataAccessException exp) {
            genre = null;
        }

        return Optional.ofNullable(genre);
    }

    @Override
    public List<Genre> getGenreById(final List<Integer> idList) {
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }

        final String sql = SELECT_GENRE + " WHERE id iN (:ids);";
        final SqlParameterSource parameters = new MapSqlParameterSource("ids", idList);

        final List<Genre> genres = namedParameterJdbcTemplate.query(sql, parameters, genreRowMapper);
        return genres;
    }

    @Override
    public List<Genre> getAllGenres() {
        final List<Genre> genres = jdbcTemplate.query(SELECT_GENRE + ";", genreRowMapper);
        return genres;
    }
}
