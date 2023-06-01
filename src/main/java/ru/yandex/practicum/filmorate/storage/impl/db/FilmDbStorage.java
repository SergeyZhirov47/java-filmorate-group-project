package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.common.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.Date;
import java.sql.*;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private static final String SELECT_FILM = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"release_date\", f.\"duration\", mr.\"id\" AS id_rating, mr.\"name\" AS name_rating\n" +
            "FROM \"films\" f\n" +
            "LEFT JOIN \"MPA_ratings\" mr ON mr.\"id\" = f.\"mpa_rating_id\"";
    private final static String SELECT_FILM_BY_ID = SELECT_FILM + " WHERE f.\"id\" = ?";
    private final static String SELECT_FILM_GENRES = "SELECT f.\"id\" as id_film, g.\"id\" as id_genre, g.\"name\" as name_genre\n" +
            "FROM \"films\" f\n" +
            "LEFT JOIN \"film_genre\" fg ON fg.\"film_id\" = f.\"id\"\n" +
            "LEFT JOIN \"genres\" g ON g.\"id\" = fg.\"genre_id\"\n";

    @Qualifier("genreDbStorage")
    protected final GenreStorage genreStorage;
    @Qualifier("mpaDbStorage")
    protected final MPAStorage mpaStorage;

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FilmRowMapper filmRowMapper = new FilmRowMapper();

    // ToDo
    // Жанры не список (List), а Set по идее должен быть. Не должно быть повторов.
    // Проработать этот момент (валидация?)

    @Override
    public Optional<Film> get(int id) {
        Film film;
        try {
            film = jdbcTemplate.queryForObject(SELECT_FILM_BY_ID + ";", filmRowMapper, id);
        } catch (EmptyResultDataAccessException exp) {
            film = null;
        }

        // еще нужно достать жанры
        if (nonNull(film)) {
            final List<Genre> filmGenres = getFilmGenres(id);
            film.setGenres(filmGenres);
        }

        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> get(final List<Integer> idList) {
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }

        final String sql = SELECT_FILM + " WHERE f.\"id\" IN (:ids);";
        final SqlParameterSource parameters = new MapSqlParameterSource("ids", idList);
        final List<Film> films = namedParameterJdbcTemplate.query(sql, parameters, filmRowMapper);

        final Map<Integer, List<Genre>> filmsGenres = getFilmGenres(idList);
        films.forEach(f -> {
            int filmId = f.getId();
            f.setGenres(filmsGenres.get(filmId));
        });

        return films;
    }

    @Override
    public List<Film> getAll() {
        final List<Film> films = jdbcTemplate.query(SELECT_FILM + ";", filmRowMapper);

        final Map<Integer, List<Genre>> filmsGenres = getFilmGenres();
        films.forEach(f -> {
            int filmId = f.getId();
            f.setGenres(filmsGenres.get(filmId));
        });

        return films;
    }

    @Override
    public int add(final Film film) {
        checkRatingExists(film);
        checkGenresExists(film);

        final String insertFilmSql = "INSERT into \"films\" (name, description, release_date, duration, mpa_rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement stmt = connection.prepareStatement(insertFilmSql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());

            if (nonNull(film.getRating())) {
                stmt.setInt(5, film.getRating().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            return stmt;
        }, keyHolder);

        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(filmId);

        // Сохраняем жанры фильма
        saveFilmGenres(film);

        return filmId;
    }

    @Override
    public void update(final Film film) {
        final int filmId = film.getId();

        checkFilmExists(filmId);
        checkRatingExists(film);
        checkRatingExists(film);

        final String updateFilmSql = "UPDATE \"films\" SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?" +
                "WHERE id = ?";

        final Integer ratingId = isNull(film.getRating()) ? null : film.getRating().getId();
        jdbcTemplate.update(updateFilmSql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), ratingId, filmId);

        // обновляем жанры фильма (сначала удаляем все, потом записываем текущие).
        deleteFilmGenre(film.getId());
        saveFilmGenres(film);
    }

    @Override
    public void delete(final Film film) {
        final int filmId = film.getId();
        deleteById(filmId);
    }

    @Override
    public void deleteById(int id) {
        final String sqlQuery = "DELETE FROM \"films\" " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public boolean contains(int id) {
        final String sql = "SELECT EXISTS(SELECT f.id " +
                "FROM \"films\" f " +
                "WHERE f.id = ?);";
        final Boolean isExists = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExists;
    }

    private void checkFilmExists(int filmId) {
        if (!contains(filmId)) {
            throw new NotFoundException(ErrorMessageUtil.getNoFilmWithIdMessage(filmId));
        }
    }

    private void saveFilmGenres(final Film film) {
        if (isNull(film.getGenres())) {
            return;
        }

        int filmId = film.getId();
        final String insertFilmGenresSql = "INSERT INTO \"film_genre\"\n" +
                "(\"film_id\", \"genre_id\")\n" +
                "VALUES(?, ?);";
        jdbcTemplate.batchUpdate(insertFilmGenresSql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, film.getGenres().get(i).getId());
            }

            public int getBatchSize() {
                return film.getGenres().size();
            }
        });
    }

    private void deleteFilmGenre(int filmId) {
        final String deleteFilmGenresSql = "DELETE FROM \"film_genre\" " +
                "WHERE film_id = ?";
        jdbcTemplate.update(deleteFilmGenresSql, filmId);
    }

    private List<Genre> getFilmGenres(int filmId) {
        final String selectFilmGenres = SELECT_FILM_GENRES + " WHERE f.\"id\" = ?;";

        final List<Genre> filmGenres = jdbcTemplate.query(selectFilmGenres, rs -> {
            final List<Genre> resultList = new ArrayList<>();

            while (rs.next()) {
                final Genre genre = getGenreFromResultSet(rs);

                if (nonNull(genre)) {
                    resultList.add(genre);
                }
            }

            return resultList;
        }, filmId);

        // если жанров у фильма нет, то должен быть пустой список
        return Objects.requireNonNullElse(filmGenres, Collections.emptyList());
    }

    private Map<Integer, List<Genre>> getFilmGenres() {
        final String sql = SELECT_FILM_GENRES + ";";
        return jdbcTemplate.query(sql, this::extractFilmGenres);
    }

    private Map<Integer, List<Genre>> getFilmGenres(final List<Integer> filmIds) {
        if (filmIds.isEmpty()) {
            return new HashMap<>();
        }
        final String sql = SELECT_FILM_GENRES + " WHERE f.\"id\" IN (:ids);";
        final SqlParameterSource parameters = new MapSqlParameterSource("ids", filmIds);

        return namedParameterJdbcTemplate.query(sql, parameters, this::extractFilmGenres);
    }

    private Map<Integer, List<Genre>> extractFilmGenres(final ResultSet rs) throws SQLException {
        final Map<Integer, List<Genre>> resultMap = new HashMap<>();

        while (rs.next()) {
            int filmId = rs.getInt("id_film");
            final Genre genre = getGenreFromResultSet(rs);

            // если жанров у фильма нет, то должен быть пустой список
            if (!resultMap.containsKey(filmId)) {
                resultMap.put(filmId, new ArrayList<>());
            }

            if (nonNull(genre)) {
                resultMap.get(filmId).add(genre);
            }
        }

        return resultMap;
    }

    private Genre getGenreFromResultSet(final ResultSet rs) throws SQLException {
        Genre genre = null;

        int genreId = rs.getInt("id_genre");
        if (!rs.wasNull()) {
            genre = Genre.builder()
                    .id(genreId)
                    .name(rs.getString("name_genre"))
                    .build();
        }

        return genre;
    }

    // ToDo
    // или придумать другое исключение или при IllegalArgumentException выдавать bad request
    private void checkRatingExists(final Film film) {
        if (nonNull(film.getRating())) {
            int ratingId = film.getRating().getId();
            boolean ratingNotExists = mpaStorage.getMPARatingById(ratingId).isEmpty();
            if (ratingNotExists) {
                throw new IllegalArgumentException(String.format("Не существует рейтинга с id = %s", ratingId));
            }
        }
    }

    private void checkGenresExists(final Film film) {
        if (nonNull(film.getGenres())) {
            final List<Integer> genresListIds = film.getGenres().stream().map(Genre::getId).collect(toUnmodifiableList());
            boolean genresNotExists = genreStorage.getGenreById(genresListIds).isEmpty();
            if (genresNotExists) {
                throw new IllegalArgumentException(String.format("Не существует жанра/жанров с id из списка %s", genresListIds.stream().map(String::valueOf).collect(joining(", "))));
            }
        }
    }
}
