package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private static final String SELECT_FILM = "SELECT f.\"id\", f.\"name\", f.\"description\", f.\"release_date\", " +
            "f.\"duration\", mr.\"id\" AS id_rating, mr.\"name\" AS name_rating\n" +
            "FROM \"films\" f\n" +
            "LEFT JOIN \"MPA_ratings\" mr ON mr.\"id\" = f.\"mpa_rating_id\"";
    private static final String SELECT_FILM_AND_LIKES = "SELECT f.\"id\", f.\"name\", f.\"description\", " +
            "f.\"release_date\", f.\"duration\", " +
            "mr.\"id\" AS id_rating, mr.\"name\" AS name_rating, COUNT(l.\"id_user\") AS likesCount\n" +
            "FROM \"films\" f\n" +
            "LEFT JOIN \"MPA_ratings\" mr ON mr.\"id\" = f.\"mpa_rating_id\" \n" +
            "LEFT JOIN \"likes\" l ON l.\"id_film\" = f.\"id\" \n" +
            "GROUP BY f.\"id\" \n" +
            "ORDER BY likesCount DESC";
    private static final String SELECT_FILM_BY_ID = SELECT_FILM + " WHERE f.\"id\" = ?";
    private static final String SELECT_FILM_GENRES = "SELECT f.\"id\" as id_film, g.\"id\" as id_genre, " +
            "g.\"name\" as name_genre\n" +
            "FROM \"films\" f\n" +
            "LEFT JOIN \"film_genre\" fg ON fg.\"film_id\" = f.\"id\"\n" +
            "LEFT JOIN \"genres\" g ON g.\"id\" = fg.\"genre_id\"\n";
    private static final String SELECT_FILM_DIRECTORS = "SELECT f.\"id\" as id_film, d.\"director_id\" as id_director, "
            + "d.\"name\" as name_director\n" +
            "FROM \"films\" f\n" +
            "LEFT JOIN \"films_directors\" fd ON fd.\"film_id\" = f.\"id\"\n" +
            "LEFT JOIN \"directors\" d ON d.\"director_id\" = fd.\"director_id\"\n";

    protected final GenreStorage genreStorage;
    protected final MPAStorage mpaStorage;
    protected final DirectorStorage directorStorage;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FilmRowMapper filmRowMapper = new FilmRowMapper();

    @Override
    public Optional<Film> get(int id) {
        Film film;
        try {
            film = jdbcTemplate.queryForObject(SELECT_FILM_BY_ID + ";", filmRowMapper, id);
        } catch (EmptyResultDataAccessException exp) {
            film = null;
        }

        // еще нужно достать жанры и режиссеров
        if (nonNull(film)) {
            final Set<Genre> filmGenres = getFilmGenres(id);
            film.setGenres(filmGenres);
        }
        if (nonNull(film)) {
            final Set<Director> filmDirectors = getFilmDirectors(id);
            film.setDirectors(filmDirectors);
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
        setFilmGenres(films, getFilmGenres(idList));

        return films;
    }

    @Override
    public List<Film> getAll() {
        final List<Film> films = jdbcTemplate.query(SELECT_FILM + ";", filmRowMapper);
        setFilmGenres(films, getFilmGenres());
        setFilmDirectors(films, getFilmDirectors());

        return films;
    }

    @Override
    public int add(final Film film) {
        checkRatingExists(film);
        checkGenresExists(film);
        checkDirectorExists(film);
        final String insertFilmSql = "INSERT into \"films\" (name, description, release_date, duration, mpa_rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement stmt = connection.prepareStatement(insertFilmSql, new String[]{"id"});
            stmt.setString(FilmInsertColumn.NAME.getColumnIndex(), film.getName());
            stmt.setString(FilmInsertColumn.DESCRIPTION.getColumnIndex(), film.getDescription());
            stmt.setDate(FilmInsertColumn.RELEASE_DATE.getColumnIndex(), Date.valueOf(film.getReleaseDate()));
            stmt.setInt(FilmInsertColumn.DURATION.getColumnIndex(), film.getDuration());

            if (nonNull(film.getRating())) {
                stmt.setInt(FilmInsertColumn.MPA_RATING.getColumnIndex(), film.getRating().getId());
            } else {
                stmt.setNull(FilmInsertColumn.MPA_RATING.getColumnIndex(), Types.INTEGER);
            }

            return stmt;
        }, keyHolder);

        int filmId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(filmId);

        // Сохраняем жанры и режиссеров фильма
        saveFilmGenres(film);
        saveFilmDirectors(film);

        return filmId;
    }

    @Override
    public void update(final Film film) {
        final int filmId = film.getId();

        checkFilmExists(filmId);
        checkRatingExists(film);
        checkDirectorExists(film);


        final String updateFilmSql = "UPDATE \"films\" SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?" +
                "WHERE id = ?";

        final Integer ratingId = isNull(film.getRating()) ? null : film.getRating().getId();
        jdbcTemplate.update(updateFilmSql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), ratingId, filmId);

        // обновляем жанры и режиссеров фильма (сначала удаляем все, потом записываем текущие).
        deleteFilmGenre(film.getId());
        saveFilmGenres(film);
        deleteFilmDirector(film.getId());
        saveFilmDirectors(film);
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

    @Override
    public List<Film> getPopular(Optional<Integer> count) {
        String selectPopular = SELECT_FILM_AND_LIKES;

        if (count.isPresent()) {
            selectPopular += " LIMIT " + count.get();
        }

        final List<Film> films = jdbcTemplate.query(selectPopular + ";", filmRowMapper);
        final List<Integer> idList = films.stream().map(Film::getId).collect(toUnmodifiableList());
        setFilmGenres(films, getFilmGenres(idList));
        setFilmDirectors(films, getFilmDirectors(idList));
        return films;
    }

    @Override
    public List<Film> getSortedFilmByDirector(String param, int directorId) {
        String sql = "SELECT f.\"id\", f.\"name\", f.\"description\" , f.\"release_date\" , f.\"duration\" , f.\"mpa_rating_id\"  FROM \"films\" f\n"
                + "RIGHT JOIN \"films_directors\" fd ON fd.\"film_id\" = f.\"id\" \n"
                + "WHERE \"director_id\" = ?\n";
        switch (param) {
            case ("year"):
                sql += "ORDER BY SELECT EXTRACT (YEAR FROM f.\"release_date\");";
                break;
            case ("likes"):
                sql += "";
        }
        return null;
    }

    @Override
    public List<Film> search(String query, String by) {
        final List<Film> films = new ArrayList<>(getPopular(Optional.empty()));
        final List<Film> validatedFilms = new ArrayList<>();
        final String lowerCaseQuery = query.toLowerCase();
        switch (by) {
            case ("title"):
                for (Film film : films) {
                    if (film.getName().toLowerCase().contains(lowerCaseQuery)) {
                        validatedFilms.add(film);
                    }
                }
                break;
            case ("director"):
                for (Film film : films) {
                    for (Director director : film.getDirectors()) {
                        if (director.getName().toLowerCase().contains(lowerCaseQuery)) {
                            validatedFilms.add(film);
                        }
                    }
                }
                break;
            default:
                final String[] splitBy = by.split(",");
                if (splitBy.length == 2
                        && (splitBy[0].equals("title")
                        && splitBy[1].equals("director")
                        || splitBy[1].equals("title")
                        && splitBy[0].equals("director"))) {
                    for (Film film : films) {
                        if (film.getName().toLowerCase().contains(lowerCaseQuery)) {
                            validatedFilms.add(film);
                        }
                        for (Director director : film.getDirectors()) {
                            if (director.getName().toLowerCase().contains(lowerCaseQuery)) {
                                validatedFilms.add(film);
                            }
                        }
                    }
                    break;
                }
        }
        return validatedFilms;
    }

    public List<Film> getPopularByGenresAndYear(Optional<Integer> count, Optional<Integer> genreId, Optional<Integer> year) {
        List<Film> filmList = getPopular(count);
        if (genreId.isPresent()) {
            filmList = filmList.stream()
                    .filter(film -> film.getGenres() != null)
                    .filter(film -> film.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList())
                            .contains(genreId.get()))
                    .collect(Collectors.toList());
        }
        if (year.isPresent()) {
            filmList = filmList.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year.get())
                    .collect(Collectors.toList());
        }
        return filmList;
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

        final List<Integer> genresIds = film.getGenres().stream().map(Genre::getId).collect(toUnmodifiableList());

        jdbcTemplate.batchUpdate(insertFilmGenresSql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, genresIds.get(i));
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

    private void setFilmGenres(final List<Film> films, final Map<Integer, Set<Genre>> filmsGenres) {
        if (!films.isEmpty()) {
            films.forEach(f -> {
                f.setGenres(filmsGenres.get(f.getId()));
            });
        }
    }

    private Map<Integer, Set<Genre>> getFilmGenres() {
        final String sql = SELECT_FILM_GENRES + " ORDER BY g.\"id\";";
        return jdbcTemplate.query(sql, this::extractFilmGenres);
    }

    private Set<Genre> getFilmGenres(int filmId) {
        final String selectFilmGenres = SELECT_FILM_GENRES + " WHERE f.\"id\" = ? ORDER BY g.\"id\";";

        final Set<Genre> filmGenres = jdbcTemplate.query(selectFilmGenres, rs -> {
            final Set<Genre> resultList = new LinkedHashSet<>();

            while (rs.next()) {
                final Genre genre = getGenreFromResultSet(rs);

                if (nonNull(genre)) {
                    resultList.add(genre);
                }
            }

            return resultList;
        }, filmId);

        // если жанров у фильма нет, то должен быть пустой список
        return Objects.requireNonNullElse(filmGenres, Collections.emptySet());
    }

    private Map<Integer, Set<Genre>> getFilmGenres(final List<Integer> filmIds) {
        if (filmIds.isEmpty()) {
            return new HashMap<>();
        }
        final String sql = SELECT_FILM_GENRES + " WHERE f.\"id\" IN (:ids) ORDER BY g.\"id\";";
        final SqlParameterSource parameters = new MapSqlParameterSource("ids", filmIds);

        return namedParameterJdbcTemplate.query(sql, parameters, this::extractFilmGenres);
    }

    private Map<Integer, Set<Genre>> extractFilmGenres(final ResultSet rs) throws SQLException {
        final Map<Integer, Set<Genre>> resultMap = new HashMap<>();

        while (rs.next()) {
            int filmId = rs.getInt("id_film");
            final Genre genre = getGenreFromResultSet(rs);

            // если жанров у фильма нет, то должен быть пустой список
            if (!resultMap.containsKey(filmId)) {
                resultMap.put(filmId, new LinkedHashSet<>());
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

    private void checkRatingExists(final Film film) {
        if (nonNull(film.getRating())) {
            int ratingId = film.getRating().getId();
            boolean ratingNotExists = mpaStorage.getMPARatingById(ratingId).isEmpty();
            if (ratingNotExists) {
                throw new ValidationException(String.format("Не существует рейтинга с id = %s", ratingId));
            }
        }
    }

    private void checkGenresExists(final Film film) {
        if (nonNull(film.getGenres())) {
            final List<Integer> genresListIds = film.getGenres().stream().map(Genre::getId).collect(toUnmodifiableList());
            boolean genresNotExists = genreStorage.getGenreById(genresListIds).isEmpty();
            if (genresNotExists) {
                throw new ValidationException(String.format("Не существует жанра/жанров с id из списка %s", genresListIds.stream().map(String::valueOf).collect(joining(", "))));
            }
        }
    }

    private Map<Integer, Set<Director>> getFilmDirectors(final List<Integer> filmIds) {
        if (filmIds.isEmpty()) {
            return new HashMap<>();
        }
        final String sql = SELECT_FILM_DIRECTORS + " WHERE f.\"id\" IN (:ids) ORDER BY d.\"director_id\";";
        final SqlParameterSource parameters = new MapSqlParameterSource("ids", filmIds);

        return namedParameterJdbcTemplate.query(sql, parameters, this::extractFilmDirectors);
    }

    private Map<Integer, Set<Director>> getFilmDirectors() {
        final String sql = SELECT_FILM_DIRECTORS + " ORDER BY d.\"director_id\";";
        return jdbcTemplate.query(sql, this::extractFilmDirectors);
    }

    private Map<Integer, Set<Director>> extractFilmDirectors(final ResultSet rs) throws SQLException {
        final Map<Integer, Set<Director>> resultMap = new HashMap<>();

        while (rs.next()) {
            int filmId = rs.getInt("id_film");
            final Director director = getDirectorFromResultSet(rs);

            // если режиссеров у фильма нет, то должен быть пустой список
            if (!resultMap.containsKey(filmId)) {
                resultMap.put(filmId, new LinkedHashSet<>());
            }

            if (nonNull(director)) {
                resultMap.get(filmId).add(director);
            }
        }

        return resultMap;
    }

    private void saveFilmDirectors(final Film film) {
        if (isNull(film.getDirectors())) {
            return;
        }

        int filmId = film.getId();
        final String insertFilmDirectorsSql = "INSERT INTO \"films_directors\"\n" +
                "(\"film_id\", \"director_id\")\n" +
                "VALUES(?, ?);";

        final List<Integer> directorIds = film.getDirectors()
                .stream().map(Director::getId).collect(toUnmodifiableList());

        jdbcTemplate.batchUpdate(insertFilmDirectorsSql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, filmId);
                ps.setInt(2, directorIds.get(i));
            }

            public int getBatchSize() {
                return film.getDirectors().size();
            }
        });
    }

    private void setFilmDirectors(final List<Film> films,
                                  final Map<Integer, Set<Director>> filmsDirectors) {
        if (!films.isEmpty()) {
            films.forEach(f -> {
                f.setDirectors(filmsDirectors.get(f.getId()));
            });
        }
    }

    private Set<Director> getFilmDirectors(int filmId) {
        final String selectFilmDirectors = SELECT_FILM_DIRECTORS + " WHERE f.\"id\" = ? ORDER BY d.\"director_id\";";

        final Set<Director> filmGenres = jdbcTemplate.query(selectFilmDirectors, rs -> {
            final Set<Director> resultList = new LinkedHashSet<>();

            while (rs.next()) {
                final Director director = getDirectorFromResultSet(rs);

                if (nonNull(director)) {
                    resultList.add(director);
                }
            }

            return resultList;
        }, filmId);

        // если режиссеров у фильма нет, то должен быть пустой список
        return Objects.requireNonNullElse(filmGenres, Collections.emptySet());
    }

    private void deleteFilmDirector(int filmId) {
        final String deleteFilmDirectorsSql = "DELETE FROM \"films_directors\" " +
                "WHERE film_id = ?";
        jdbcTemplate.update(deleteFilmDirectorsSql, filmId);
    }

    private Director getDirectorFromResultSet(final ResultSet rs) throws SQLException {
        Director director = null;

        int directorId = rs.getInt("id_director");
        if (!rs.wasNull()) {
            director = Director.builder()
                    .id(directorId)
                    .name(rs.getString("name_director"))
                    .build();
        }

        return director;
    }

    private void checkDirectorExists(final Film film) {
        if (nonNull(film.getDirectors())) {
            final List<Integer> directorsListIds = film.getDirectors().stream()
                    .map(Director::getId).collect(toUnmodifiableList());
            final boolean directorsNotExists = directorStorage.getDirectorsByIds(directorsListIds)
                    .isEmpty();
            if (directorsNotExists) {
                throw new ValidationException(String.format("Не существует режиссера/режиссеров с id из списка %s",
                        directorsListIds.stream().map(String::valueOf).collect(joining(", "))));
            }
        }
    }

    @AllArgsConstructor
    private enum FilmInsertColumn {
        NAME(1),
        DESCRIPTION(2),
        RELEASE_DATE(3),
        DURATION(4),
        MPA_RATING(5);

        @Getter
        private final int columnIndex;
    }
}
