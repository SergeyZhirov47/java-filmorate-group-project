package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.MPAStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MPAService {
    protected final MPAStorage mpaStorage;

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
}
