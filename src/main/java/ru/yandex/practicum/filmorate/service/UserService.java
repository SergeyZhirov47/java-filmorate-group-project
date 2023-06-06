package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    protected final UserStorage userStorage;
    protected final FriendStorage friendStorage;

    public int add(User user) {
        user.setEmptyNameAsLogin();
        final int userId = userStorage.add(user);

        return userId;
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
            throw new NotFoundException(ErrorMessageUtil.getNoUserWithIdMessage(id));
        }

        return userOpt.get();
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public void addFriend(int userId, int friendId) {
        checkUserExists(userId);
        checkFriendExists(friendId);

        friendStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        checkUserExists(userId);
        checkFriendExists(friendId);

        friendStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        checkUserExists(userId);

        final List<Integer> friendIds = friendStorage.getFriends(userId);
        return getUserListByIds(friendIds);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        checkUserExists(userId);
        checkOtherUserExists(otherUserId);

        return friendStorage.getCommonFriends(userId, otherUserId);
    }

    private List<User> getUserListByIds(final List<Integer> userIds) {
        return userStorage.get(userIds);
    }

    private boolean isUserExists(int id) {
        return userStorage.contains(id);
    }

    private void checkUserExistsWithException(int id, final String message) {
        if (!isUserExists(id)) {
            throw new NotFoundException(message);
        }
    }

    private void checkUserExists(int id) {
        checkUserExistsWithException(id, ErrorMessageUtil.getNoUserWithIdMessage(id));
    }

    private void checkFriendExists(int id) {
        checkUserExistsWithException(id, ErrorMessageUtil.getNoFriendWithIdMessage(id));
    }

    private void checkOtherUserExists(int id) {
        checkUserExistsWithException(id, String.format("Нет пользователя (с кем должны быть общие друзья) с id = %s", id));
    }
}