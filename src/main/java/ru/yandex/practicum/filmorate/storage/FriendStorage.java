package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
        void addFriend(int userId, int friendId);
        void deleteFriend(int userId, int friendId);
        List<Integer> getFriends(int userId);
        List<Integer> getCommonFriends(int userId, int otherUserId);
}
