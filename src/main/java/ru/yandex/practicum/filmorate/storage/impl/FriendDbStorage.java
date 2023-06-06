package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.List;

@Component
@Qualifier("friendDbStorage")
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private static final String SELECT_ID_FRIENDS = "SELECT f.id_friend " +
            "FROM \"friendship\" f " +
            "WHERE f.id_user = ? AND f.is_accepted = TRUE";
    private static final String SELECT_FRIENDS = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
            "FROM \"users\" u ";
    private final JdbcTemplate jdbcTemplate;

    private final UserRowMapper userRowMapper = new UserRowMapper();

    @Override
    public void addFriend(int userId, int friendId) {
        addFriendOneWay(userId, friendId, true);
        addFriendOneWay(friendId, userId, false);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        // Удаляем только одну запись
        final String sql = "DELETE FROM friendship " +
                "WHERE id_user = ? AND id_friend = ?";

        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        final List<User> userFriends = jdbcTemplate.query(SELECT_FRIENDS + " WHERE u.id IN (" + SELECT_ID_FRIENDS + ")", userRowMapper, userId);
        return userFriends;
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        final String SELECT_ID_FRIENDS_EXCEPT = SELECT_ID_FRIENDS + " AND f.id_friend != ? "; // берем друзей для userId исключая otherUserId (если они друг у друга в друзьях).
        final String commonFriendIds = SELECT_ID_FRIENDS_EXCEPT + " UNION " + SELECT_ID_FRIENDS_EXCEPT;
        final String sql = SELECT_FRIENDS + " WHERE u.id IN (" + commonFriendIds + ");";

        final List<User> commonFriends = jdbcTemplate.query(sql, userRowMapper, userId, otherUserId, otherUserId, userId);
        return commonFriends;
    }

    private boolean contains(int userId, int friendId) {
        final String sql = "SELECT EXISTS(SELECT id " +
                "FROM \"friendship\" " +
                "WHERE id_user = ? AND id_friend = ?);";
        final Boolean isExists = jdbcTemplate.queryForObject(sql, Boolean.class, userId, friendId);
        return isExists;
    }

    private void addFriendOneWay(int userId, int friendId, boolean isAccepted) {
        String sql;

        if (contains(userId, friendId)) {
            sql = "UPDATE \"friendship\"\n" +
                    "SET is_accepted = TRUE\n" +
                    "WHERE id_user = ? AND id_friend = ?;";
        } else {
            sql = "INSERT INTO \"friendship\"\n" +
                    "(\"id_user\", \"id_friend\", \"is_accepted\")\n" +
                    "VALUES(?, ?, " + isAccepted + ");";
        }

        jdbcTemplate.update(sql, userId, friendId);
    }
}
