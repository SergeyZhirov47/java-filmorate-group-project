package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

import java.util.*;
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

    @Override
    public List<Integer> getPopular(int count) {
        final Comparator<Map.Entry<Integer, Set<Integer>>> likesCountDescComparator = Comparator.<Map.Entry<Integer, Set<Integer>>>comparingInt(kv -> kv.getValue().size()).reversed();

        return filmLikes.entrySet().stream()
                .sorted(likesCountDescComparator)
                .map(Map.Entry::getKey)
                .limit(count)
                .collect(Collectors.toUnmodifiableList());
    }

    private boolean filmContains(int id) {
        return filmLikes.containsKey(id);
    }
}
