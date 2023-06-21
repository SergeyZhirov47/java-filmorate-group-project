package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public void removeDirector(int id) {
        directorStorage.delete(id);
    }
}
