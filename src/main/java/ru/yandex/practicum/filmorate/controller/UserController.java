package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User newUser) {
        log.info("POST /users");
        userService.add(newUser);
        log.info(String.format("Добавлен новый пользователь %s", newUser));

        return newUser;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User update(@Valid @RequestBody User user) {
        log.info("PUT /users");
        userService.update(user);
        log.info(String.format("Обновлена информация о пользователе с id %s", user.getId()));

        return user;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        log.info("GET /users");
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable int id) {
        log.info("GET /users/{id}");
        return userService.getById(id);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable int id) {
        log.info("DELETE /users/{id}");
        userService.deleteById(id);
    }

    /*
    PUT /users/{id}/friends/{friendId} — добавление в друзья.
    DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    */

    @PutMapping("/users/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @GetMapping("/users/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    List<User> getAllFriends(@PathVariable int id) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    List<User> getSameFriends() {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
