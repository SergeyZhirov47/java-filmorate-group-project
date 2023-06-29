package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.common.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Event> getEventsByUserId(int id) {
        String sql = "SELECT \"id\", \"last_update\", \"id_user\", \"event_type\", \"operation\", \"entity_id\"\n" +
                     "  FROM \"events\"\n" +
                     " WHERE \"id_user\" = ?";

        List<Event> events = jdbcTemplate.query(sql, new EventRowMapper(), id);

        return events;
    }

    public Event addEvent(Event event) {
        String sql = "INSERT INTO \"events\" (\"id_user\", \"event_type\", \"operation\", \"entity_id\", \"last_update\") " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP())";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setInt(1, event.getUserId());
            stmt.setString(2, event.getEventType());
            stmt.setString(3, event.getOperation());
            stmt.setInt(4, event.getEntityId());
            return stmt;
        }, keyHolder);

        Integer id = keyHolder.getKey().intValue();
        event.setEventId(id);

        return event;
    }

    public Event addEvent(int userId, int entityId, String eventType, String operation) {
        Event event = Event.builder()
                        .userId(userId)
                        .entityId(entityId)
                        .eventType(eventType)
                        .operation(operation)
                        .build();

        return addEvent(event);
    }


    public void deleteEventReview(int reviewId) {
        String sql = "DELETE FROM \"events\" WHERE \"entity_id\" = ? AND \"event_type\" = ?";

        jdbcTemplate.update(sql, reviewId, "REVIEW");
    }

    public void deleteEventUser(int userId) {
        String sql = "";
    }
}
