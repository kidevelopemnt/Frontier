package frontier.engine.events;

public class Event {
    private Object source;
    private Object context;

    public Object getSource() {
        return source;
    }

    public Object getContext() {
        return context;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public void setContext(Object context) {
        this.context = context;
    }
}
