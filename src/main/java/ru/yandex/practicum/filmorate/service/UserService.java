package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    protected UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void add(User user) {
        user.setEmptyNameAsLogin();
        userStorage.add(user);
    }

    public void update(User user) {
        user.setEmptyNameAsLogin();
        userStorage.update(user);
    }

    public void deleteById(int id) {
        userStorage.deleteById(id);
    }

    public User getById(int id) {
        final Optional<User> userOpt = userStorage.get(id);

        if (userOpt.isEmpty()) {
            throw new NotFoundException(String.format("Нет пользователя с id = %s", id));
        }

        return userOpt.get();
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }
}