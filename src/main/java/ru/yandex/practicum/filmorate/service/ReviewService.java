package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.common.ErrorMessageUtil;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewLikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    protected final int defaultReviewsLimit = 10;

    protected final ReviewStorage reviewStorage;
    protected final ReviewLikeStorage reviewLikeStorage;

    // ToDo
    // проверки на наличие фильма, пользователя и обзора перенести сюда из ReviewDbStorage

    public int add(final Review review) {
        return reviewStorage.add(review);
    }

    public void update(final Review review) {
        // ToDo
        // наверное должны проверять, что filmId и userId не поменялись у отзыва (в тестах постмана этот момент не учитывается).
        reviewStorage.update(review);
    }

    public List<Review> getByFilmId(Optional<Integer> filmId, Optional<Integer> count) {
        final Optional<Integer> finalCount = Optional.of(count.orElse(defaultReviewsLimit));
        return reviewStorage.getByFilmId(filmId, finalCount);
    }

    public void deleteById(int id) {
        reviewStorage.deleteById(id);
    }

    public Review getById(int id) {
        final Optional<Review> reviewOpt = reviewStorage.get(id);

        if (reviewOpt.isEmpty()) {
            throw new NotFoundException(ErrorMessageUtil.getNoEntityWithIdMessage("Нет отзыва", id));
        }

        return reviewOpt.get();
    }

    public void addLike(int reviewId, int userId) {
        reviewLikeStorage.addLike(reviewId, userId);
    }

    public void addDislike(int reviewId, int userId) {
        reviewLikeStorage.addDislike(reviewId, userId);
    }

    public void deleteLike(int reviewId, int userId) {
        reviewLikeStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(int reviewId, int userId) {
        reviewLikeStorage.deleteDislike(reviewId, userId);
    }
}
