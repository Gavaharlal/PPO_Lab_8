package statistic;

import clock.Clock;
import clock.SystemClock;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class EventStatisticImpl implements EventsStatistic {
    private final Queue<Event> pushedEvents = new ArrayDeque<>();
    private final Map<String, Integer> eventsCount = new HashMap<>();
    private final Clock clock;

    public EventStatisticImpl(Clock clock) {
        this.clock = clock;
    }

    private void refreshEvents() {
        long nowInMillis = clock.now().toEpochMilli();
        final long MILLIS_IN_HOUR = Duration.ofHours(1).toMillis();
        while (!pushedEvents.isEmpty()) {
            Event tmp = pushedEvents.peek();
            if (tmp.getTime() + MILLIS_IN_HOUR <= nowInMillis) {
                pushedEvents.remove();
                Integer count = eventsCount.get(tmp.getName());
                if (count == 1) {
                    eventsCount.remove(tmp.getName());
                } else {
                    eventsCount.put(tmp.getName(), count - 1);
                }
            } else {
                break;
            }
        }
    }

    private double getStatPerMinute(Integer count) {
        return count == null ? 0 : 1.0 * count / Duration.ofHours(1).toMinutes();
    }

    @Override
    public void incEvent(String name) {
        refreshEvents();
        long now = clock.now().toEpochMilli();
        pushedEvents.add(new Event(name, now));
        eventsCount.putIfAbsent(name, 0);
        eventsCount.put(name, eventsCount.get(name) + 1);
    }

    @Override
    public double getEventStatisticByName(String name) {
        refreshEvents();
        return getStatPerMinute(eventsCount.get(name));
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        refreshEvents();
        Map<String, Double> allStat = new HashMap<>();
        for (Map.Entry<String, Integer> eventEntry: eventsCount.entrySet()) {
            allStat.put(eventEntry.getKey(), getStatPerMinute(eventEntry.getValue()));
        }
        return allStat;
    }

    @Override
    public void printStatistic() {
        refreshEvents();
        for (Map.Entry<String, Integer> eventEntry: eventsCount.entrySet()) {
            System.out.println(eventEntry.getKey() + " = " + eventEntry.getValue());
        }
    }

    public static void main(String[] args) {
        EventsStatistic eventsStatistic = new EventStatisticImpl(new SystemClock());
        eventsStatistic.incEvent("eveng");
        eventsStatistic.incEvent("eveng");
        eventsStatistic.incEvent("ev2");
        eventsStatistic.printStatistic();
    }
}
