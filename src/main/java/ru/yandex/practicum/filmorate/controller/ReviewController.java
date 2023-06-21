package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    // Добавление нового отзыва.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review add(@Valid @RequestBody Review newReview) {
        log.info("POST /reviews");
        throw new UnsupportedOperationException("not implemented!!!");
    }

    // Редактирование уже имеющегося отзыва.
    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("PUT /reviews");
        throw new UnsupportedOperationException("not implemented!!!");
    }

    // Удаление уже имеющегося отзыва.
    @DeleteMapping("/{id}")
    public void delete(@PathVariable(name = "id") int id) {
        throw new UnsupportedOperationException("not implemented!!!");
    }

    // Получение отзыва по идентификатору.
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable(name = "id") int id) {
        throw new UnsupportedOperationException("not implemented!!!");
    }

    //  Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.
    @GetMapping
    public List<Review> getReviewsByFilm(@RequestParam(name = "filmId") Optional<Integer> filmId, @RequestParam(name = "count") Optional<Integer> count) {
        log.info(String.format("GET /reviews?filmId={filmId}&count={count}, {filmId} = %s, {count} = %s", filmId.isPresent() ? filmId.get() : "не указан", count.isPresent() ? count.get() : "не указан"));
        throw new UnsupportedOperationException("not implemented!!!");
    }

    // пользователь ставит лайк отзыву.
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        throw new UnsupportedOperationException("not implemented!!!");
    }

    // пользователь ставит дизлайк отзыву.
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        throw new UnsupportedOperationException("not implemented!!!");
    }

    // пользователь удаляет лайк отзыву.
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        throw new UnsupportedOperationException("not implemented!!!");
    }

    // пользователь удаляет дизлайк отзыву.
    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable(name = "id") int id, @PathVariable(name = "userId") int userId) {
        throw new UnsupportedOperationException("not implemented!!!");
    }
}
