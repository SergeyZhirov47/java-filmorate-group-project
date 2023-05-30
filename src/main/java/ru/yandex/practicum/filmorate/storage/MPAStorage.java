package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Optional;

public interface MPAStorage {
    Optional<MPA> getMPARatingById(int id);

    List<MPA> getAllMPARatings();
}
