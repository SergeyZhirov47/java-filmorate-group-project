package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.common.IdGenerator;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Integer, Genre> GENRES_MAP = new HashMap<>();
    private static final Map<Integer, MPA> MPA_RATINGS_MAP = new HashMap<>();

    static {
        GENRES_MAP.put(1, new Genre(1, "Комедия"));
        GENRES_MAP.put(2, new Genre(2, "Драма"));
        GENRES_MAP.put(3, new Genre(3, "Мультфильм"));
        GENRES_MAP.put(4, new Genre(4, "Триллер"));
        GENRES_MAP.put(5, new Genre(5, "Документальный"));
        GENRES_MAP.put(6, new Genre(6, "Боевик"));

        MPA_RATINGS_MAP.put(1, new MPA(1, "G"));
        MPA_RATINGS_MAP.put(2, new MPA(2, "PG"));
        MPA_RATINGS_MAP.put(3, new MPA(3, "PG-13"));
        MPA_RATINGS_MAP.put(4, new MPA(4, "R"));
        MPA_RATINGS_MAP.put(5, new MPA(5, "NC-17"));
    }

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
    public boolean contains(int id) {
        return films.containsKey(id);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        return Optional.ofNullable(GENRES_MAP.get(id));
    }

    @Override
    public List<Genre> getAllGenres() {
        return GENRES_MAP.values().stream().collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<MPA> getMPARatingById(int id) {
        return Optional.ofNullable(MPA_RATINGS_MAP.get(id));
    }

    @Override
    public List<MPA> getAllMPARatings() {
        return MPA_RATINGS_MAP.values().stream().collect(Collectors.toUnmodifiableList());
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

        if (contains(filmId)) {
            films.put(filmId, film);
        } else {
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
        if (contains(id)) {
            films.remove(id);
        } else {
            throw new NotFoundException(ErrorMessageUtil.getFilmDeleteFailMessage(id));
        }
    }
}
