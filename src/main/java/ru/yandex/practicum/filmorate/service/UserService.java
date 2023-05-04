package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    protected UserStorage userStorage;
    protected FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
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

    public void addFriend(int userId, int friendId) {
        checkUserExistsWithException(userId, String.format("Нет пользователя с id = %s", userId));
        checkUserExistsWithException(friendId, String.format("Нет пользователя (друга) с id = %s", friendId));

        friendStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        checkUserExistsWithException(userId, String.format("Нет пользователя с id = %s", userId));
        checkUserExistsWithException(friendId, String.format("Нет пользователя (друга) с id = %s", friendId));

        friendStorage.addFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        checkUserExistsWithException(userId, String.format("Нет пользователя с id = %s", userId));

        final List<Integer> friendIds = friendStorage.getFriends(userId);
        return getUserListByIds(friendIds);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        checkUserExistsWithException(userId, String.format("Нет пользователя с id = %s", userId));
        checkUserExistsWithException(otherUserId, String.format("Нет пользователя (с кем должны быть общие друзья) с id = %s", otherUserId));

        final List<Integer> commonFriendIds = friendStorage.getCommonFriends(userId, otherUserId);
        return getUserListByIds(commonFriendIds);
    }

    private List<User> getUserListByIds(final List<Integer> userIds) {
        return userIds.stream().map(id -> userStorage.get(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableList());
    }

    private boolean isUserExists(int id) {
        return userStorage.get(id).isPresent();
    }

    private void checkUserExistsWithException(int id, final String message) {
       if (!isUserExists(id)) {
           throw new NotFoundException(message);
       }
    }
}