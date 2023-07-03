package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    protected final UserStorage userStorage;
    protected final FriendStorage friendStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

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

        eventStorage.addEvent(userId, friendId, EventType.FRIEND, Operation.ADD);
    }

    public void deleteFriend(int userId, int friendId) {
        checkUserExists(userId);
        checkFriendExists(friendId);

        friendStorage.deleteFriend(userId, friendId);

        eventStorage.addEvent(userId, friendId, EventType.FRIEND, Operation.REMOVE);
    }

    public List<User> getFriends(int userId) {
        checkUserExists(userId);

        return friendStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        checkUserExists(userId);
        checkOtherUserExists(otherUserId);

        return friendStorage.getCommonFriends(userId, otherUserId);
    }

    public List<Film> getRecommendedFilms(int id) {
        return filmStorage.get(userStorage.getRecommendedFilmsForUser(id));
    }

    public List<Event> getEvents(int id) {
        checkUserExists(id);
        return eventStorage.getEventsByUserId(id);
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