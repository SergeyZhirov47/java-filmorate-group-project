package ru.yandex.practicum.filmorate.common.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("id"))
                .entityId(rs.getInt("entity_id"))
                .userId(rs.getInt("id_user"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .timestamp(rs.getDate("last_update").getTime())
                .build();
    }
}