package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.List;

import static java.util.Objects.isNull;

@Component
@Qualifier("friendDbStorage")
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    // private final UserRowMapper userRowMapper = new UserRowMapper();

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
        // неважно принял другой пользователь дружбу или нет
        final String sql = "SELECT id_friend " +
                "FROM friendship " +
                "WHERE id_user = ?";

        // ToDo
        // 1. или тут нужно возвращать список пользователей, а не id
        // 2. или в UserStorage сделать метод, который по списку id сразу список пользователей возвращать, а не как сейчас по одному доставать
        // final List<User> userFriends = jdbcTemplate.query(sql, userRowMapper);

        final List<Integer> userFriends = jdbcTemplate.queryForList(sql, Integer.class, userId);
        return userFriends;
    }

    // ToDo
    // getCommonFriends
    // т.е список общих друзей проще получить тут или в UserStorage
    private List<Integer> getCommonFriends(int userId, int otherUserId) {
        final String sql = "SELECT id_friend FROM friendship WHERE id_user = ?" +
                "UNION " +
                "SELECT id_friend FROM friendship WHERE id_user = ?";

        final List<Integer> commonFriends = jdbcTemplate.queryForList(sql, Integer.class, userId, otherUserId);
        return commonFriends;
    }

    private boolean contains(int userId, int friendId) {
        final String sql = "SELECT id " +
                "FROM \"friendship\" " +
                "WHERE id_user = ? AND id_friend = ?";
        final Integer friendshipId = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);

        return !isNull(friendshipId);
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
