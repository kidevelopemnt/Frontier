package frontier.engine.events;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
    private final Map<EventKey, List<Consumer<Event>>> register = new HashMap<>();

    public <T extends Event> void subscribe(Object source, Class<T> eventClass, Consumer<Event> callback) {
        // TODO: Allow subscribing to no specific source
        EventKey key = new EventKey(source, eventClass);
        if (register.containsKey(key)) {
            register.get(key).add(callback);
        } else {
            register.put(key, List.of(callback));
        }
    }

    public <T extends Event> void trigger(Object source, Class<T> eventClass, Object context) {
        Event event = null;
        try {
            event = eventClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        EventKey key = new EventKey(source, eventClass);

        event.setSource(source);
        event.setContext(context);
        if (register.containsKey(key)) {
            for (Consumer<Event> callback : register.get(key)) {
                callback.accept(event);
            }
        }
    }

    public <T extends Event> void trigger(Object source, Class<T> eventClass) {
        trigger(source, eventClass, null);
    }
}
