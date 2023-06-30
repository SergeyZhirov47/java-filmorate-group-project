package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Event {
    private int eventId;
    private int entityId;
    private int userId;
    private String eventType;
    private String operation;
    private long timestamp;
}
