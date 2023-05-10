package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.common.IdGenerator;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    @Override
    public Optional<User> get(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public int add(User user) {
        final int newId = idGenerator.getNext();
        user.setId(newId);

        users.put(newId, user);
        return newId;
    }

    @Override
    public void update(User user) {
        final int userId = user.getId();

        if (users.containsKey(userId)) {
            users.put(userId, user);
        } else {
            // throw new NotFoundException(String.format("Нет пользователя с id = %s. Обновление не успешно.", userId));
            throw new NotFoundException(ErrorMessageUtil.getUserUpdateFailMessage(userId));
        }
    }

    @Override
    public void delete(User user) {
        final int userId = user.getId();
        deleteById(userId);
    }

    @Override
    public void deleteById(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            // throw new NotFoundException(String.format("Нет пользователя с id = %s. Удаление не успешно.", id));
            throw new NotFoundException(ErrorMessageUtil.getUserDeleteFailMessage(id));
        }
    }
}
