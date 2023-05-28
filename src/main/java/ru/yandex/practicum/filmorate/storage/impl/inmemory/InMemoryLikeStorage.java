package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component
public class InMemoryLikeStorage implements LikeStorage {
    protected Map<Integer, Set<Integer>> filmLikes = new HashMap<>();

    @Override
    public void registerFilm(int filmId) {
        if (!filmContains(filmId)) {
            filmLikes.put(filmId, new HashSet<>());
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        final Set<Integer> userHowLikes = filmLikes.get(filmId);
        if (!isNull(userHowLikes)) {
            userHowLikes.add(userId);
        } else {
            final Set<Integer> usersHowLikes = new HashSet<>();
            usersHowLikes.add(userId);
            filmLikes.put(filmId, usersHowLikes);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (filmContains(filmId)) {
            filmLikes.get(filmId).remove(userId);
        }
    }

    // Ключ - id фильма, значение - кол-во пользователей кому фильм понравился.
    @Override
    public Map<Integer, Integer> getFilmLikes() {
        return filmLikes.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey,
                        v -> v.getValue().size()));
    }

    private boolean filmContains(int id) {
        return filmLikes.containsKey(id);
    }
}
