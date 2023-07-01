package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {

    protected final FilmStorage filmStorage;
    protected final LikeStorage likeStorage;
    protected final UserStorage userStorage;
    protected final EventStorage eventStorage;

    public int add(Film film) {
        final int filmId = filmStorage.add(film);
        return filmId;
    }

    public void update(Film film) {
        filmStorage.update(film);
    }

    public Film getById(int id) {
        checkFilmExists(id);

        final Optional<Film> filmOpt = filmStorage.get(id);
        return filmOpt.get();
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        likeStorage.addLike(filmId, userId);

        eventStorage.addEvent(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    public void deleteLike(int filmId, int userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        likeStorage.removeLike(filmId, userId);

        eventStorage.addEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }


    public List<Film> getPopularByGenresAndYear(Optional<Integer> count, Optional<Integer> genreId,
                                                Optional<Integer> year) {
       return filmStorage.getPopularByGenresAndYear(count, genreId, year);
    }

    public void deleteFilmById(int filmId) {
        checkFilmExists(filmId);
        filmStorage.deleteById(filmId);
    }

    private boolean isUserExists(int id) {
        return userStorage.contains(id);
    }

    private boolean isFilmExists(int id) {
        return filmStorage.contains(id);
    }

    private void checkExistsWithException(boolean exists, final String message) {
        if (!exists) {
            throw new NotFoundException(message);
        }
    }

    private void checkFilmExists(int id) {
        checkExistsWithException(isFilmExists(id), ErrorMessageUtil.getNoFilmWithIdMessage(id));
    }

    private void checkUserExists(int id) {
        checkExistsWithException(isUserExists(id), ErrorMessageUtil.getNoUserWithIdMessage(id));
    }

    public List<Film> getSortedFilmByDirector(String param, int directorId) {
        return filmStorage.getSortedFilmByDirector(param, directorId);
    }

    public List<Film> search(String query, String by) {
        return filmStorage.search(query, by);
    }
}
