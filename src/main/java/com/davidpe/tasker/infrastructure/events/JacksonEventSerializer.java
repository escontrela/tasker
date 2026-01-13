package com.davidpe.tasker.infrastructure.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.modulith.events.core.EventSerializer;
import org.springframework.stereotype.Component;

@Component
public class JacksonEventSerializer implements EventSerializer {

    private final ObjectMapper objectMapper;

    public JacksonEventSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object serialize(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize event", ex);
        }
    }

    @Override
    public <T> T deserialize(Object serialized, Class<T> type) {
        try {
            return objectMapper.readValue(String.valueOf(serialized), type);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize event", ex);
        }
    }
}
