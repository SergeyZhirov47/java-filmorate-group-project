package ru.yandex.practicum.filmorate.storage.impl.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.common.IdGenerator;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    @Qualifier("inmemoryGenreStorage")
    protected final GenreStorage genreStorage;
    @Qualifier("inmemoryMPAStorage")
    protected final MPAStorage mpaStorage;

    @Override
    public Optional<Film> get(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> get(List<Integer> idList) {
        final List<Film> result = films.values().stream()
                .filter(f -> idList.contains(f.getId()))
                .collect(Collectors.toUnmodifiableList());

        return result;
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
    public int add(Film film) {
        checkRatingExists(film);
        checkGenresExists(film);

        final int newId = idGenerator.getNext();
        film.setId(newId);

        films.put(newId, film);
        return newId;
    }

    @Override
    public void update(Film film) {
        checkRatingExists(film);
        checkGenresExists(film);

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

    private void checkRatingExists(final Film film) {
        if (nonNull(film.getRating())) {
            int ratingId = film.getRating().getId();
            boolean ratingNotExists = mpaStorage.getMPARatingById(ratingId).isEmpty();
            if (ratingNotExists) {
                throw new ValidationException(String.format("Не существует рейтинга с id = %s", ratingId));
            }
        }
    }

    private void checkGenresExists(final Film film) {
        if (nonNull(film.getGenres())) {
            final List<Integer> genresListIds = film.getGenres().stream().map(Genre::getId).collect(toUnmodifiableList());
            boolean genresNotExists = genreStorage.getGenreById(genresListIds).isEmpty();
            if (genresNotExists) {
                throw new ValidationException(String.format("Не существует жанра/жанров с id из списка %s", genresListIds.stream().map(String::valueOf).collect(joining(", "))));
            }
        }
    }
}
