package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.List;

public interface EventStorage {
    List<Event> getEventsByUserId(int id);

    Event addEvent(Event event);

    Event addEvent(int userId, int entityId, EventType eventType, Operation operation);
}
