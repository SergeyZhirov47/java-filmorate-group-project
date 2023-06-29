package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    List<Event> getEventsByUserId(int id);

    Event addEvent(Event event);

    Event addEvent(int userId, int entityId, String eventType, String operation);

    void deleteEventReview(int reviewId);

    void deleteEventUser(int userId);
}
