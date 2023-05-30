package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("likeDbStorage")
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void registerFilm(int filmId) {
        // Теперь этот метод не нужен. А он в интерфейсе...
        // throw new UnsupportedOperationException("do nothing in this implementation");
    }

    @Override
    public void addLike(int filmId, int userId) {
        // ToDo
        // Если лайк уже есть, то ничего не делаем.
        final String sql = "INSERT INTO \"likes\"\n" +
                "(\"id_film\", \"id_user\")\n" +
                "VALUES(? , ?);";

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        final String sql = "DELETE FROM \"likes\"\n" +
                "WHERE id_film = ? AND id_user = ?;";

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Map<Integer, Integer> getFilmLikes() {
        final String sql = "SELECT f.\"id\" as id_film, COUNT(l.\"id_user\") AS likesCount\n" +
                "FROM \"films\" f \n" +
                "LEFT JOIN \"likes\" l ON l.\"id_film\" = f.\"id\" \n" +
                "GROUP BY f.\"id\" \n" +
                "ORDER BY likesCount DESC;";

        final Map<Integer, Integer> allFilmsLikes = jdbcTemplate.query(sql, (ResultSetExtractor<Map<Integer, Integer>>) rs -> {
            final HashMap<Integer, Integer> resultMap = new HashMap<>();

            while (rs.next()) {
                resultMap.put(rs.getInt("id_film"), rs.getInt("likesCount"));
            }
            return resultMap;
        });

        return allFilmsLikes;
    }
}
