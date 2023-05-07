package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface LikeStorage {
    // ToDo
    // костыль? в выдаче популярных фильмов могут быть фильмы и без лайков. поэтому проще всего добавлять фильм и делать ему пустое множество лайкнувших.
    void registerFilm(int filmId);
    void addLike(int filmId, int userId);
    void removeLike(int filmId, int userId);
    List<Integer> getPopular(int count);
}
