package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int lastId = 0;

    private int nextId() {
        lastId++;
        return lastId;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User newUser) {
        if (StringUtils.isBlank(newUser.getName())) {
            newUser.setName(newUser.getLogin());
        }

        final int newId = nextId();
        newUser.setId(newId);
        users.put(newId, newUser);

        log.info(String.format("Добавлен новый пользователь %s", newUser));

        return newUser;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User update(@Valid @RequestBody User user) {
        final int userId = user.getId();
        if (users.containsKey(userId)) {
            users.put(userId, user);
        } else {
            throw new NotFoundException(String.format("Нет пользователя с id = %s. Обновление не успешно.", userId));
        }

        log.info(String.format("Обновлена информация о пользователе с id %s", userId));

        return user;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        return users.values().stream().collect(Collectors.toUnmodifiableList());
    }
}
