package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.common.IdGenerator;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    @Override
    public Optional<Film> get(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getAll() {
        return films.values().stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public int add(Film film) {
        final int newId = idGenerator.getNext();
        film.setId(newId);

        films.put(newId, film);
        return newId;
    }

    @Override
    public void update(Film film) {
        final int filmId = film.getId();

        if (films.containsKey(filmId)) {
            films.put(filmId, film);
        } else {
            // throw new NotFoundException(String.format("Нет фильма с id = %s. Обновление не успешно.", filmId));
            throw new NotFoundException(ErrorMessageUtil.getFilmUpdateFailMessage(filmId));
        }
    }

    @Override
    public void delete(Film film) {
        final int filmId = film.getId();
        deleteById(filmId);
    }

    @Override
    public void deleteById(int id) {
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            // throw new NotFoundException(String.format("Нет фильма с id = %s. Удаление не успешно.", id));
            throw new NotFoundException(ErrorMessageUtil.getFilmDeleteFailMessage(id));
        }
    }


}
