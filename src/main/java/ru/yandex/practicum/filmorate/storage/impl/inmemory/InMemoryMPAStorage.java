package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryMPAStorage implements MPAStorage {
    private static final Map<Integer, MPA> MPA_RATINGS_MAP = new HashMap<>();

    static {
        MPA_RATINGS_MAP.put(1, new MPA(1, "G"));
        MPA_RATINGS_MAP.put(2, new MPA(2, "PG"));
        MPA_RATINGS_MAP.put(3, new MPA(3, "PG-13"));
        MPA_RATINGS_MAP.put(4, new MPA(4, "R"));
        MPA_RATINGS_MAP.put(5, new MPA(5, "NC-17"));
    }

    @Override
    public Optional<MPA> getMPARatingById(int id) {
        return Optional.ofNullable(MPA_RATINGS_MAP.get(id));
    }

    @Override
    public List<MPA> getAllMPARatings() {
        return MPA_RATINGS_MAP.values().stream().collect(Collectors.toUnmodifiableList());
    }
}
