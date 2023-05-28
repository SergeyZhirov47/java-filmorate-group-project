package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class InMemoryFriendStorage implements FriendStorage {
    // Ключ - id пользователя
    // Значение - набор id пользователей, которые являются друзьями "ключа"
    // Значения хранятся зеркально. Если у пользователя с id = 1 добавлен друг с id = 101, то будут внесены две записи.
    // у 1 добавился друг 101. у 101 добавился друг 1.
    protected final Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Override
    public void addFriend(int userId, int friendId) {
        addFriendOneWay(userId, friendId);
        addFriendOneWay(friendId, userId);
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

        return userFriends.stream().collect(Collectors.toUnmodifiableList());
    }

    private void addFriendOneWay(int userId, int friendId) {
        if (friends.containsKey(userId)) {
            friends.get(userId).add(friendId);
        } else {
            final Set<Integer> friendSet = new HashSet<>();
            friendSet.add(friendId);

            friends.put(userId, friendSet);
        }
    }
}
