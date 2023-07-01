package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Data
@Builder
@AllArgsConstructor
public class Event {
    private int eventId;
    private int entityId;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private long timestamp;
}
