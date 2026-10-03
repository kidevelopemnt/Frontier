package frontier.engine.events;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
    private final Map<Class<? extends Event>, List<Consumer<Event>>> register = new HashMap<>();

    public <T extends Event> void subscribe(Class<T> eventClass, Consumer<Event> callback) {
        if (register.containsKey(eventClass)) {
            register.get(eventClass).add(callback);
        } else {
            register.put(eventClass, List.of(callback));
        }
    }

    public <T extends Event> void trigger(Class<T> eventClass) {
        Event event = null;
        try {
            event = eventClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        if (register.containsKey(eventClass)) {
            for (Consumer<Event> callback : register.get(eventClass)) {
                callback.accept(event);
            }
        }
    }
}
