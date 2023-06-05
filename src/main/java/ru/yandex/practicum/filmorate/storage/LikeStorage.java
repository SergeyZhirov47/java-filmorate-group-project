package ru.yandex.practicum.filmorate.storage;

import java.util.Map;

public interface LikeStorage {
    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Map<Integer, Integer> getFilmLikes();
}
