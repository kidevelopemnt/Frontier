package frontier.engine.events;

public record EventKey(Object source, Class<? extends Event> eventType) {
    // Constructor validation (optional but recommended)
    public EventKey {
        java.util.Objects.requireNonNull(source, "Source cannot be null");
        java.util.Objects.requireNonNull(eventType, "Event type cannot be null");
    }
}