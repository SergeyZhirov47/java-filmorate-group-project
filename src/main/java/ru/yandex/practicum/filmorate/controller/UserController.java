package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@Valid @PathVariable int id) {
        log.info("GET /users/{id}");
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@Valid @PathVariable int id) {
        log.info("DELETE /users/{id}");
        userService.deleteById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@Valid @PathVariable int id, @Valid @PathVariable int friendId) {
        log.info("PUT /users/{id}/friends/{friendId}");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@Valid @PathVariable int id, @Valid @PathVariable int friendId) {
        log.info("DELETE /users/{id}/friends/{friendId}");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    List<User> getAllFriends(@Valid @PathVariable int id) {
        log.info("GET /users/{id}/friends");
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    List<User> getCommonFriends(@PathVariable(name="id") int id, @PathVariable int otherId) {
        log.info("GET /users/{id}/friends/common/{otherId}");
        return userService.getCommonFriends(id, otherId);
    }
}
