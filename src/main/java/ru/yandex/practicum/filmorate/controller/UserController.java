package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
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
    public User update(@Valid @RequestBody User user) {
        log.info("PUT /users");
        userService.update(user);
        log.info(String.format("Обновлена информация о пользователе с id %s", user.getId()));

        return userService.getById(user.getId());
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("GET /users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable(name = "id") int id) {
        log.info(String.format("GET /users/{id}, {id} = %s", id));
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable(name = "id") int id) {
        log.info(String.format("DELETE /users/{id}, {id} = %s", id));
        userService.deleteById(id);
        log.info(String.format("Пользователь с id = %s успешно удален", id));
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable(name = "id") int id, @PathVariable(name = "friendId") int friendId) {
        log.info(String.format("PUT /users/{id}/friends/{friendId}, {id} = %s, {friendId} = %s", id, friendId));
        userService.addFriend(id, friendId);
        log.info(String.format("Пользователь с id = %s успешно добавил в друзья пользователя с id = %s", id, friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable(name = "id") int id, @PathVariable(name = "friendId") int friendId) {
        log.info(String.format("DELETE /users/{id}/friends/{friendId}, {id} = %s, {friendId} = %s", id, friendId));
        userService.deleteFriend(id, friendId);
        log.info(String.format("Пользователь с id = %s успешно удалил из друзей пользователя с id = %s", id, friendId));
    }

    @GetMapping("/{id}/friends")
    List<User> getAllFriends(@PathVariable(name = "id") int id) {
        log.info(String.format("GET /users/{id}/friends, {id} = %s", id));
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    List<User> getCommonFriends(@PathVariable(name = "id") int id, @PathVariable(name = "otherId") int otherId) {
        log.info(String.format("GET /users/{id}/friends/common/{otherId}, {id} = %s, {otherId} = %s}", id, otherId));
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("{id}/recommendations")
    public List<Film> getRecommendedFilms(@PathVariable int id) {
        log.info(String.format("GET /films/%s/recommendations", id));
        return userService.getRecommendedFilms(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getEventsOfUser(@PathVariable(name = "id") int userId) {
        log.info(String.format("GET /users/{id}/feed, {id} = %s", userId));
        return userService.getEvents(userId);
    }
}
