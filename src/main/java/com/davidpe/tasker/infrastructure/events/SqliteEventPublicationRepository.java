package com.davidpe.tasker.infrastructure.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.modulith.events.core.EventPublicationRepository;
import org.springframework.modulith.events.core.EventSerializer;
import org.springframework.modulith.events.core.PublicationTargetIdentifier;
import org.springframework.modulith.events.core.TargetEventPublication;
import org.springframework.stereotype.Repository;

@Repository
public class SqliteEventPublicationRepository implements EventPublicationRepository {

    private static final String TABLE_NAME = "event_publication";

    private final JdbcTemplate jdbcTemplate;
    private final EventSerializer eventSerializer;

    public SqliteEventPublicationRepository(JdbcTemplate jdbcTemplate, EventSerializer eventSerializer) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventSerializer = eventSerializer;
    }

    @Override
    public TargetEventPublication create(TargetEventPublication publication) {
        String serializedEvent = String.valueOf(eventSerializer.serialize(publication.getEvent()));
        jdbcTemplate.update(
                "INSERT INTO " + TABLE_NAME + " (id, listener_id, event_type, serialized_event, publication_date, completion_date, attempts, last_error) "
                        + "VALUES (?, ?, ?, ?, ?, ?, 0, NULL)",
                publication.getIdentifier().toString(),
                publication.getTargetIdentifier().getValue(),
                publication.getEvent().getClass().getName(),
                serializedEvent,
                publication.getPublicationDate().toString(),
                publication.getCompletionDate().map(Instant::toString).orElse(null));
        return publication;
    }

    @Override
    public void markCompleted(Object event, PublicationTargetIdentifier identifier, Instant completionDate) {
        String serializedEvent = String.valueOf(eventSerializer.serialize(event));
        jdbcTemplate.update(
                "UPDATE " + TABLE_NAME
                        + " SET completion_date = ? "
                        + "WHERE listener_id = ? AND event_type = ? AND serialized_event = ? AND completion_date IS NULL",
                completionDate.toString(),
                identifier.getValue(),
                event.getClass().getName(),
                serializedEvent);
    }

    @Override
    public List<TargetEventPublication> findIncompletePublications() {
        return jdbcTemplate.query(
                "SELECT * FROM " + TABLE_NAME + " WHERE completion_date IS NULL",
                new EventPublicationRowMapper());
    }

    @Override
    public List<TargetEventPublication> findIncompletePublicationsPublishedBefore(Instant instant) {
        return jdbcTemplate.query(
                "SELECT * FROM " + TABLE_NAME + " WHERE completion_date IS NULL AND publication_date < ?",
                new EventPublicationRowMapper(),
                instant.toString());
    }

    @Override
    public Optional<TargetEventPublication> findIncompletePublicationsByEventAndTargetIdentifier(Object event,
            PublicationTargetIdentifier identifier) {
        String serializedEvent = String.valueOf(eventSerializer.serialize(event));
        List<TargetEventPublication> publications = jdbcTemplate.query(
                "SELECT * FROM " + TABLE_NAME
                        + " WHERE completion_date IS NULL AND listener_id = ? AND event_type = ? AND serialized_event = ?",
                new EventPublicationRowMapper(),
                identifier.getValue(),
                event.getClass().getName(),
                serializedEvent);
        return publications.stream().findFirst();
    }

    @Override
    public void deletePublications(List<UUID> ids) {
        if (ids.isEmpty()) {
            return;
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        List<Object> args = new ArrayList<>(ids.size());
        ids.forEach(id -> args.add(id.toString()));
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE id IN (" + placeholders + ")", args.toArray());
    }

    @Override
    public void deleteCompletedPublications() {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE completion_date IS NOT NULL");
    }

    @Override
    public void deleteCompletedPublicationsBefore(Instant instant) {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE completion_date IS NOT NULL AND completion_date < ?",
                instant.toString());
    }

    private class EventPublicationRowMapper implements RowMapper<TargetEventPublication> {

        @Override
        public TargetEventPublication mapRow(ResultSet rs, int rowNum) throws SQLException {
            UUID id = UUID.fromString(rs.getString("id"));
            String listenerId = rs.getString("listener_id");
            String eventType = rs.getString("event_type");
            String serializedEvent = rs.getString("serialized_event");
            Instant publicationDate = Instant.parse(rs.getString("publication_date"));
            String completionDateRaw = rs.getString("completion_date");
            Optional<Instant> completionDate = completionDateRaw == null
                    ? Optional.empty()
                    : Optional.of(Instant.parse(completionDateRaw));
            Object event = deserializeEvent(eventType, serializedEvent);
            return new StoredEventPublication(id, event, PublicationTargetIdentifier.of(listenerId), publicationDate,
                    completionDate);
        }
    }

    private Object deserializeEvent(String eventType, String serializedEvent) {
        try {
            @SuppressWarnings("unchecked")
            Class<Object> eventClass = (Class<Object>) Class.forName(eventType);
            return eventSerializer.deserialize(serializedEvent, eventClass);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Unknown event type: " + eventType, ex);
        }
    }

    private static final class StoredEventPublication implements TargetEventPublication {

        private final UUID identifier;
        private final Object event;
        private final PublicationTargetIdentifier targetIdentifier;
        private final Instant publicationDate;
        private Optional<Instant> completionDate;

        private StoredEventPublication(UUID identifier, Object event, PublicationTargetIdentifier targetIdentifier,
                Instant publicationDate, Optional<Instant> completionDate) {
            this.identifier = identifier;
            this.event = event;
            this.targetIdentifier = targetIdentifier;
            this.publicationDate = publicationDate;
            this.completionDate = completionDate;
        }

        @Override
        public UUID getIdentifier() {
            return identifier;
        }

        @Override
        public Object getEvent() {
            return event;
        }

        @Override
        public PublicationTargetIdentifier getTargetIdentifier() {
            return targetIdentifier;
        }

        @Override
        public Instant getPublicationDate() {
            return publicationDate;
        }

        @Override
        public Optional<Instant> getCompletionDate() {
            return completionDate;
        }

        @Override
        public void markCompleted(Instant completionDate) {
            this.completionDate = Optional.ofNullable(completionDate);
        }
    }
}
