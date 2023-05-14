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
        final int userId = userService.add(newUser);
        log.info(String.format("Добавлен новый пользователь %s", newUser));

        return userService.getById(userId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User update(@Valid @RequestBody User user) {
        log.info("PUT /users");
        userService.update(user);
        log.info(String.format("Обновлена информация о пользователе с id %s", user.getId()));

        return userService.getById(user.getId());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        log.info("GET /users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable(name = "id") int id) {
        log.info(String.format("GET /users/{id}, {id} = %s", id));
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable(name = "id") int id) {
        log.info(String.format("DELETE /users/{id}, {id} = %s", id));
        userService.deleteById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable(name = "id") int id, @PathVariable(name = "friendId") int friendId) {
        log.info(String.format("PUT /users/{id}/friends/{friendId}, {id} = %s, {friendId} = %s", id, friendId));
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable(name = "id") int id, @PathVariable(name = "friendId") int friendId) {
        log.info(String.format("DELETE /users/{id}/friends/{friendId}, {id} = %s, {friendId} = %s", id, friendId));
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    List<User> getAllFriends(@PathVariable(name = "id") int id) {
        log.info(String.format("GET /users/{id}/friends, {id} = %s", id));
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    List<User> getCommonFriends(@PathVariable(name = "id") int id, @PathVariable(name = "otherId") int otherId) {
        log.info(String.format("GET /users/{id}/friends/common/{otherId}, {id} = %s, {otherId} = %s}", id, otherId));
        return userService.getCommonFriends(id, otherId);
    }
}
