package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.common.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private static final String SELECT_USER = "SELECT \"id\", \"email\", \"login\", \"name\", \"birthday\"\n" +
            "FROM \"users\"";
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserRowMapper userRowMapper = new UserRowMapper();

    @Override
    public Optional<User> get(int id) {
        User user;
        try {
            user = jdbcTemplate.queryForObject(SELECT_USER + " WHERE id = ?;", userRowMapper, id);
        } catch (EmptyResultDataAccessException exp) {
            user = null;
        }

        return Optional.ofNullable(user);
    }

    @Override
    public List<User> get(final List<Integer> idList) {
        if (idList.isEmpty()) {
            return new ArrayList<>();
        }

        final String sql = SELECT_USER + " WHERE id IN (:ids);";
        final SqlParameterSource parameters = new MapSqlParameterSource("ids", idList);

        final List<User> users = namedParameterJdbcTemplate.query(sql, parameters, userRowMapper);
        return users;
    }

    @Override
    public int add(final User user) {
        final String sqlQuery = "INSERT INTO \"users\" (\"email\", \"login\", \"name\", \"birthday\")\n" +
                "VALUES(?, ?, ?, ?);";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(UserInsertColumn.EMAIL.getColumnIndex(), user.getEmail());
            stmt.setString(UserInsertColumn.LOGIN.getColumnIndex(), user.getLogin());
            stmt.setString(UserInsertColumn.NAME.getColumnIndex(), user.getName());
            stmt.setDate(UserInsertColumn.BIRTHDAY.getColumnIndex(), Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(final User user) {
        final int userId = user.getId();

        checkUserExists(userId);

        final String sqlQuery = "UPDATE \"users\" " +
                "SET \"email\" = ?, \"login\" = ?, \"name\" = ?, \"birthday\" = ? " +
                "WHERE \"id\" = ?;";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), userId);
    }

    @Override
    public void delete(final User user) {
        final int userId = user.getId();
        deleteById(userId);
    }

    @Override
    public void deleteById(int id) {
        final String sqlQuery = "DELETE FROM \"users\" " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<User> getAll() {
        final List<User> users = jdbcTemplate.query(SELECT_USER + ";", userRowMapper);
        return users;
    }

    @Override
    public List<Integer> getRecommendedFilmsForUser(int userId) {
        get(userId).orElseThrow(() -> new ValidationException("Пользователя с id не существует"));

        List<Integer> likedFilms = getLikedFilms(userId);
        if (likedFilms.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, Integer> commonLikes = new HashMap<>();

        String sqlQuery = "SELECT id_user, id_film FROM likes WHERE id_user != :userId";
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("userId", userId);

        List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sqlQuery, parameters);

        for (Map<String, Object> row : results) {
            int otherId = (int) row.get("id_user");
            int filmId = (int) row.get("id_film");

            if (likedFilms.contains(filmId)) {
                commonLikes.put(otherId, commonLikes.getOrDefault(otherId, 0) + 1);
            }
        }

        List<Integer> commonLikedUsers = new ArrayList<>(commonLikes.keySet());

        if (commonLikedUsers.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> recommendedFilms = getRecommendedFilms(userId, commonLikedUsers);

        if (recommendedFilms.isEmpty()) {
            return Collections.emptyList();
        }

        return recommendedFilms;
    }

    private List<Integer> getRecommendedFilms(int userId, List<Integer> commonLikedUsers) {
        String selectFilms = "SELECT DISTINCT f.id FROM films f " +
                "INNER JOIN likes l ON f.id = l.id_film " +
                "WHERE l.id_user IN (:userIds) AND l.id_film NOT IN " +
                "(SELECT id_film FROM likes WHERE id_user = :userId)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("userIds", commonLikedUsers)
                .addValue("userId", userId);

        return namedParameterJdbcTemplate.queryForList(selectFilms, parameters, Integer.class);
    }

    private List<Integer> getLikedFilms(int userId) {
        String sqlQuery = "SELECT id_film FROM likes WHERE id_user = :userId";
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("userId", userId);

        return namedParameterJdbcTemplate.queryForList(sqlQuery, parameters, Integer.class);
    }

    @Override
    public boolean contains(int id) {
        final String sql = "SELECT EXISTS(SELECT id " +
                "FROM \"users\" " +
                "WHERE id = ?);";
        final Boolean isExists = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExists;
    }

    private void checkUserExists(int userId) {
        if (!contains(userId)) {
            throw new NotFoundException(ErrorMessageUtil.getNoUserWithIdMessage(userId));
        }
    }

    @AllArgsConstructor
    private enum UserInsertColumn {
        EMAIL(1),
        LOGIN(2),
        NAME(3),
        BIRTHDAY(4);

        @Getter
        private final int columnIndex;
    }
}
