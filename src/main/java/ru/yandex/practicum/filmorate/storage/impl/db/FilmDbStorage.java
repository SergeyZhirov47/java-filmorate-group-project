package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.common.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

import static java.util.Objects.isNull;

@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final FilmRowMapper filmRowMapper = new FilmRowMapper();

    private final String selectFilmBase = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"release_date\", f.\"duration\", mr.\"name\" AS rating, string_agg(g.\"name\", ', ') as genre \n" +
            "FROM \"films\" f\n" +
            "LEFT JOIN \"MPA_ratings\" mr ON mr.\"id\" = f.\"mpa_rating_id\"\n" +
            "LEFT JOIN \"film_genre\" fg ON fg.\"film_id\" = f.\"id\" \n" +
            "LEFT JOIN \"genres\" g ON g.\"id\" = fg.\"genre_id\"\n";
    private final String selectAllFilms = selectFilmBase + "GROUP BY f.\"id\";";

    private final String selectFilm = selectFilmBase + "WHERE f.\"id\" = ?";

    @Override
    public Optional<Film> get(int id) {
        checkFilmExists(id);

        final Film film = jdbcTemplate.queryForObject(selectFilm, filmRowMapper, id);

        // ToDo
        // проверка того, что фильм есть еще не гарантия того что он будет прочитан.
        // что делать и как обрабатывать?

        return Optional.ofNullable(film);
    }

    @Override
    public int add(final Film film) {
        // ToDo
        // Должно ли тут бросаться исключение, если не существует указанного жанра или рейтинга? MethodArgumentNotValidException?
        // наверное да
        //final Integer genreId = isNull(film.getGenre()) ? null : getGenreId(film.getGenre());
        //final Integer ratingId = isNull(film.getRating()) ? null : getRatingId(film.getRating());

        final String sqlQuery = "INSERT into \"films\" (name, description, release_date, duration, mpa_rating_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getId());
           // stmt.setInt(5, ratingId);
            return stmt;
        }, keyHolder);

        // ToDo
        // Еще отдельно сохраняем жанры фильма

        // ToDo
        // добавление может быть неудачным. как это разрулить?
        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(final Film film) {
        final int filmId = film.getId();

        checkFilmExists(filmId);

        // ToDo
        // тут точно должно бросаться исключение если неверный жанр или рейтинг???
        // фильм уже приходит валидный?
        // если да, то как и где эта валидация выполняется?
        // если нет, то какого фига? название, дату создания значит провалидировали, а другие поля нет?
        // ToDo 2
        // а в реальных проектах как это работает? Каждый раз лезть в базу и доставать id по названию как-то не очень...
        // дополнительно в моделе id хранится? или как?
        //final Integer genreId = getGenreId(film.getGenre());
       // final Integer ratingId = getRatingId(film.getRating());

        final String sqlQuery = "UPDATE \"films\" SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), filmId);
    }

    @Override
    public void delete(final Film film) {
        final int filmId = film.getId();
        deleteById(filmId);
    }

    @Override
    public void deleteById(int id) {
        throw new UnsupportedOperationException("not implemented!");
    }

    @Override
    public List<Film> getAll() {
        final List<Film> films = jdbcTemplate.query(selectAllFilms, filmRowMapper);
        return films;
    }

    @Override
    public boolean contains(int id) {
        final String sql = "SELECT f.id " +
                "FROM \"films\" f " +
                "WHERE f.id = ?";
        final Integer filmId = jdbcTemplate.queryForObject(sql, Integer.class, id);

        return !isNull(filmId);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        return Optional.empty();
    }

    @Override
    public List<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Optional<MPA> getMPARatingById(int id) {
        return Optional.empty();
    }

    @Override
    public List<MPA> getAllMPARatings() {
        return null;
    }

    /*
    @Override
    public String getGenreById(int id) {
        final String sql = "SELECT name " +
                "FROM \"genres\" " +
                "WHERE id = ?;";

        // ToDo
        // бросать исключение если не нашел?
        final String genreName = jdbcTemplate.queryForObject(sql, String.class, id);
        return genreName;
    }

    @Override
    public Map<Integer, String> getAllGenres() {
        final String sql = "SELECT id, name " +
                "FROM \"genres\";";

        final Map<Integer, String> allGenresMap = jdbcTemplate.query(sql, (ResultSetExtractor<Map<Integer, String>>) rs -> {
            final HashMap<Integer, String> resultMap = new HashMap<>();

            while (rs.next()) {
                resultMap.put(rs.getInt("id"), rs.getString("name"));
            }
            return resultMap;
        });

        return allGenresMap;
    }


    @Override
    public String getMPARatingById(int id) {
        final String sql = "SELECT name " +
                "FROM \"MPA_ratings\" " +
                "WHERE id = ?;";

        // ToDo
        // бросать исключение если не нашел?
        final String ratingName = jdbcTemplate.queryForObject(sql, String.class, id);
        return ratingName;
    }

    @Override
    public Map<Integer, String> getAllMPARatings() {
        final String sql = "SELECT id, name " +
                "FROM \"MPA_ratings\";";

        final Map<Integer, String> allRatingsMap = jdbcTemplate.query(sql, (ResultSetExtractor<Map<Integer, String>>) rs -> {
            final HashMap<Integer, String> resultMap = new HashMap<>();

            while (rs.next()) {
                resultMap.put(rs.getInt("id"), rs.getString("name"));
            }
            return resultMap;
        });

        return allRatingsMap;
    }
     */

    // ToDo
    // А вот поиск популярных фильмов теперь лучшее (по производительности) сделать здесь или в LikeStorage

    private void checkFilmExists(int filmId) {
        if (!contains(filmId)) {
            throw new NotFoundException(ErrorMessageUtil.getNoFilmWithIdMessage(filmId));
        }
    }

    private Integer getGenreId(final String genreName) {
        final String sql = "SELECT g.id " +
                "FROM \"genre\" g " +
                "WHERE g.name = ?";
        final Integer genreId = jdbcTemplate.queryForObject(sql, Integer.class, genreName);

        return genreId;
    }

    private Integer getRatingId(final String ratingName) {
        final String sql = "SELECT r.id " +
                "FROM \"MPA_ratings\" r " +
                "WHERE r.name = ?";
        final Integer ratingId = jdbcTemplate.queryForObject(sql, Integer.class, ratingName);

        return ratingId;
    }
}
