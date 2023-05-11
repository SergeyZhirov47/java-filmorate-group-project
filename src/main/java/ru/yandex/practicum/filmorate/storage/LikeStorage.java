package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface LikeStorage {
    void registerFilm(int filmId);
    void addLike(int filmId, int userId);
    void removeLike(int filmId, int userId);
    List<Integer> getPopular(int count);
}
