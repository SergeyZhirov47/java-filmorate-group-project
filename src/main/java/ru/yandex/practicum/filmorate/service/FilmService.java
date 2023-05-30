package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableList;

@Service
@RequiredArgsConstructor
public class FilmService {
    protected final int defaultPopularLimit = 10;

    @Qualifier("filmDbStorage")
    protected final FilmStorage filmStorage;
    @Qualifier("likeDbStorage")
    protected final LikeStorage likeStorage;
    @Qualifier("userDbStorage")
    protected final UserStorage userStorage;

    @Qualifier("genreDbStorage")
    final protected GenreStorage genreStorage;
    @Qualifier("mpaDbStorage")
    final protected MPAStorage mpaStorage;

    public int add(Film film) {
        // ToDo
        // в сервисе должна происходить проверка того что рейтинг и жанр существуют?
        // если взять метод getById, то почему должен возвращаться недоделанный объект. откуда мы это знаем?
        // с другой стороны вынос этих проверок в storage не будет ли выносом бизнес логики?

        final int filmId = filmStorage.add(film);
        likeStorage.registerFilm(filmId);

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
    }

    public void deleteLike(int filmId, int userId) {
        checkFilmExists(filmId);
        checkUserExists(userId);

        likeStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(final Optional<Integer> count) {
        int finalCount = count.or(() -> Optional.of(defaultPopularLimit)).get();

        final Map<Integer, Integer> filmsLikes = likeStorage.getFilmLikes();
        final Comparator<Map.Entry<Integer, Integer>> likesCountDescComparator = Comparator.<Map.Entry<Integer, Integer>>comparingInt(Map.Entry::getValue).reversed();

        final List<Integer> popularFilmIds = filmsLikes.entrySet().stream()
                .sorted(likesCountDescComparator).map(Map.Entry::getKey).limit(finalCount)
                .collect(Collectors.toUnmodifiableList());

        return getFilmListByIds(popularFilmIds);
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        final Optional<Genre> genreOpt = genreStorage.getGenreById(id);

        if (genreOpt.isEmpty()) {
            throw new NotFoundException(String.format("Жанр с id = %s не найден", id));
        }

        return genreOpt.get();
    }

    public List<MPA> getAllMPARatings() {
        return mpaStorage.getAllMPARatings();
    }

    public MPA getMPARating(int id) {
        final Optional<MPA> ratingOpt = mpaStorage.getMPARatingById(id);

        if (ratingOpt.isEmpty()) {
            throw new NotFoundException(String.format("Рейтинг с id = %s не найден", id));
        }

        return ratingOpt.get();
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

    private List<Film> getFilmListByIds(final List<Integer> filmIds) {
        return filmIds.stream().map(id -> filmStorage.get(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toUnmodifiableList());
    }
}
