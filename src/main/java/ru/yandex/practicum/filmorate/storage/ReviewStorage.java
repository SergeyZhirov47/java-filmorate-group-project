package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> get(int id);

    int add(Review review);

    void update(Review review);

    List<Review> getByFilmId(Optional<Integer> filmId, Optional<Integer> count);

    void deleteById(int id);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);

    void addDislike(int id, int userId);

    void deleteDislike(int id, int userId);

    boolean contains(int id);
}
