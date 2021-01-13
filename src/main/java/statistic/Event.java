package statistic;

public class Event {
    private final String name;
    private final long time;

    public Event(String name, long time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }
}