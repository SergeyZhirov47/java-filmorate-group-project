package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class InMemoryFriendStorage implements FriendStorage {
    // Ключ - id пользователя
    // Значение - набор id пользователей, которые являются друзьями "ключа"
    // Значения хранятся зеркально. Если у пользователя с id = 1 добавлен друг с id = 101, то будут внесены две записи.
    // у 1 добавился друг 101. у 101 добавился друг 1.
    protected Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Override
    public void addFriend(int userId, int friendId) {
        if (friends.containsKey(userId)) {
            friends.get(userId).add(friendId);
        } else {
            final Set<Integer> friendSet = new HashSet<>();
            friendSet.add(friendId);

            friends.put(userId, friendSet);
        }
        if (friends.containsKey(friendId)) {
            friends.get(friendId).add(userId);
        } else {
            final Set<Integer> friendSet = new HashSet<>();
            friendSet.add(userId);

            friends.put(friendId, friendSet);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        final Set<Integer> userFriends = friends.get(userId);
        if (!isNull(userFriends)) {
            userFriends.remove(friendId);
        }

        final Set<Integer> friendFriends = friends.get(friendId);
        if (!isNull(friendFriends)) {
            friendFriends.remove(userId);
        }
    }

    @Override
    public List<Integer> getFriends(int userId) {
        final Set<Integer> userFriends = friends.get(userId);

        if (isNull(userFriends)) {
            return new ArrayList<>();
        }

        return friends.get(userId).stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Integer> getCommonFriends(int userId, int otherUserId) {
        final Set<Integer> userFriends = friends.get(userId);
        final Set<Integer> otherUserFriends = friends.get(otherUserId);

        // если у кого нет друзей, то пустой список
        if (isNull(userFriends) || isNull(otherUserFriends)) {
            return new ArrayList<>();
        }

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toUnmodifiableList());
    }
}
