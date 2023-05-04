package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> get(int id);
    void add(User user);
    void update(User user);
    void delete(User user);
    void deleteById(int id);
    List<User> getAll();
}
