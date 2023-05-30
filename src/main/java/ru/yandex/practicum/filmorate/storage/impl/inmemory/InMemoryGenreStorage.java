package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toUnmodifiableList;

public class InMemoryGenreStorage implements GenreStorage {
    private static final Map<Integer, Genre> GENRES_MAP = new HashMap<>();

    static {
        GENRES_MAP.put(1, new Genre(1, "Комедия"));
        GENRES_MAP.put(2, new Genre(2, "Драма"));
        GENRES_MAP.put(3, new Genre(3, "Мультфильм"));
        GENRES_MAP.put(4, new Genre(4, "Триллер"));
        GENRES_MAP.put(5, new Genre(5, "Документальный"));
        GENRES_MAP.put(6, new Genre(6, "Боевик"));
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        return Optional.ofNullable(GENRES_MAP.get(id));
    }

    @Override
    public List<Genre> getGenreById(List<Integer> idList) {
        return GENRES_MAP.entrySet().stream()
                .filter(kv -> idList.contains(kv.getKey()))
                .map(Map.Entry::getValue)
                .collect(toUnmodifiableList());
    }

    @Override
    public List<Genre> getAllGenres() {
        return GENRES_MAP.values().stream().collect(toUnmodifiableList());
    }
}
