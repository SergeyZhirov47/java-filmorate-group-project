package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.List;

@Component
@Qualifier("friendDbStorage")
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private static final String SELECT_FRIENDS = "SELECT id_friend FROM friendship WHERE id_user = ? AND is_accepted = TRUE";
    private final JdbcTemplate jdbcTemplate;

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
    public List<Integer> getFriends(int userId) {
        final List<Integer> userFriends = jdbcTemplate.queryForList(SELECT_FRIENDS + ";", Integer.class, userId);
        return userFriends;
    }

    private List<Integer> getCommonFriends(int userId, int otherUserId) {
        final String sql = SELECT_FRIENDS +
                "UNION " +
                SELECT_FRIENDS + ";";

        final List<Integer> commonFriends = jdbcTemplate.queryForList(sql, Integer.class, userId, otherUserId);
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
