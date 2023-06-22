package ru.yandex.practicum.filmorate.storage.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.common.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private static final String SELECT_REVIEW_BASE = "SELECT r.id, r.id_user, r.id_film, r.content, r.isPositive, \n" +
            "(COALESCE(rl1.likes_count, 0) - COALESCE(rl2.dislikes_count, 0)) AS rating\n" +
            "FROM \"reviews\" r\n" +
            "LEFT JOIN (SELECT id_review, COUNT(id) as likes_count " +
            "FROM \"reviews_likes\" " +
            "WHERE isUseful = TRUE " +
            "GROUP BY id_review) rl1 ON rl1.id_review = r.id\n" +
            "LEFT JOIN (SELECT id_review, COUNT(id) as dislikes_count " +
            "FROM \"reviews_likes\" " +
            "WHERE isUseful = FALSE " +
            "GROUP BY id_review) rl2 ON rl2.id_review = r.id";
    private static final String ORDER_PART = " ORDER BY rating DESC ";

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    private final ReviewRowMapper reviewRowMapper = new ReviewRowMapper();

    @Override
    public Optional<Review> get(int id) {
        Review review;
        try {
            review = jdbcTemplate.queryForObject(SELECT_REVIEW_BASE + " WHERE r.id = ?;", reviewRowMapper, id);
        } catch (EmptyResultDataAccessException exp) {
            review = null;
        }

        return Optional.ofNullable(review);
    }

    @Override
    public List<Review> getByFilmId(Optional<Integer> filmId, Optional<Integer> count) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource();

        final StringBuilder sqlStrBuilder = new StringBuilder(SELECT_REVIEW_BASE);
        if (filmId.isPresent()) {
            sqlStrBuilder.append(" WHERE r.id_film = :filmId ");
            parameters.addValue("filmId", filmId.get());

        }
        sqlStrBuilder.append(ORDER_PART);
        if (count.isPresent()) {
            sqlStrBuilder.append(" LIMIT :limitValue ");
            parameters.addValue("limitValue", count.get());
        }
        sqlStrBuilder.append(";");

        return namedParameterJdbcTemplate.query(sqlStrBuilder.toString(), parameters, reviewRowMapper);
    }

    @Override
    public int add(final Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());

        final String insertSql = "INSERT into \"reviews\" (id_user, id_film, content, isPositive) " +
                "VALUES (?, ?, ?, ?)";

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            final PreparedStatement stmt = connection.prepareStatement(insertSql, new String[]{"id"});
            stmt.setInt(ReviewInsertColumn.USER_ID.getColumnIndex(), review.getUserId());
            stmt.setInt(ReviewInsertColumn.FILM_ID.getColumnIndex(), review.getFilmId());
            stmt.setString(ReviewInsertColumn.CONTENT.getColumnIndex(), review.getContent());
            stmt.setBoolean(ReviewInsertColumn.IS_POSITIVE.getColumnIndex(), review.getIsPositive());

            return stmt;
        }, keyHolder);

        int reviewId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        review.setId(reviewId);

        return reviewId;
    }

    @Override
    public void update(final Review review) {
        final int reviewId = review.getId();

        checkReviewExists(reviewId);

        final String updateSql = "UPDATE \"reviews\"\n" +
                "SET content = ?, isPositive = ?\n" +
                "WHERE id = ?;";

        jdbcTemplate.update(updateSql, review.getContent(), review.getIsPositive(), reviewId);
    }

    @Override
    public void deleteById(int id) {
        checkReviewExists(id);

        final String sqlQuery = "DELETE FROM \"reviews\" " +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    // ToDo
    // Тут тоже работу с лайками обзоров вынести в отдельный Storage?
    @Override
    public void addLike(int id, int userId) {
        setFeedback(id, userId, true);
    }

    @Override
    public void deleteLike(int id, int userId) {
        setFeedback(id, userId, null);
    }

    @Override
    public void addDislike(int id, int userId) {
        setFeedback(id, userId, false);
    }

    @Override
    public void deleteDislike(int id, int userId) {
        setFeedback(id, userId, null);
    }

    @Override
    public boolean contains(int id) {
        final String sql = "SELECT EXISTS(SELECT r.id " +
                "FROM \"reviews\" r " +
                "WHERE r.id = ?);";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }

    private void setFeedback(int reviewId, int userId, Boolean isUseful) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("reviewId", reviewId);
        parameters.addValue("userId", userId);
        parameters.addValue("isUseful", isUseful);

        String sql;

        if (containsFeedback(reviewId, userId)) {
            sql = "UPDATE \"reviews_likes\"\n" +
                    "SET isUseful = :isUseful\n" +
                    "WHERE id_review = :reviewId AND id_user = :userId;";
        } else {
            sql = "INSERT INTO \"reviews_likes\"\n" +
                    "(id_review, id_user, isUseful)\n" +
                    "VALUES(:reviewId, :userId, :isUseful);";
        }

        namedParameterJdbcTemplate.update(sql, parameters);
    }

    private boolean containsFeedback(int reviewId, int userId) {
        final String sql = "SELECT EXISTS(SELECT id " +
                "FROM \"reviews_likes\" " +
                "WHERE id_review = ? AND id_user = ?);";
        return jdbcTemplate.queryForObject(sql, Boolean.class, reviewId, userId);
    }

    private void checkReviewExists(int reviewId) {
        if (!contains(reviewId)) {
            throw new NotFoundException(ErrorMessageUtil.getNoEntityWithIdMessage("Нет отзыва", reviewId));
        }
    }

    // ToDo
    // у UserStorage есть метод checkUserExists. Он приватный и не в интерфейсе. Но делает то что нужно.
    // Аналогично у FilmStorage есть checkFilmExists.
    // Вынести в отдельный класс... валидации? или я еще как-то. Но если уберу или поменяю, то у других ничего не поломается?
    // или фиг с ним...
    private void checkFilmExists(int filmId) {
        if (!filmStorage.contains(filmId)) {
            throw new NotFoundException(ErrorMessageUtil.getNoFilmWithIdMessage(filmId));
        }
    }

    private void checkUserExists(int userId) {
        if (!userStorage.contains(userId)) {
            throw new NotFoundException(ErrorMessageUtil.getNoUserWithIdMessage(userId));
        }
    }

    @AllArgsConstructor
    private enum ReviewInsertColumn {
        USER_ID(1),
        FILM_ID(2),
        CONTENT(3),
        IS_POSITIVE(4);

        @Getter
        private final int columnIndex;
    }
}
