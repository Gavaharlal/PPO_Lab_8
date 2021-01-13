import clock.FakeClock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import statistic.EventStatisticImpl;
import statistic.EventsStatistic;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class EventStatisticImplTest {
    private FakeClock clock;
    private EventsStatistic eventsStatistic;
    private static final double DOUBLE_PRECISION = 1e-64;

    @Before
    public void before() {
        clock = new FakeClock(Instant.now());
        eventsStatistic = new EventStatisticImpl(clock);
    }

    private void checkEventStatistic(String name, double expected) {
        Assert.assertEquals(eventsStatistic.getEventStatisticByName(name), expected, DOUBLE_PRECISION);
    }

    private void checkAllEventStatistics(Map<String, Double> expected) {
        Map<String, Double> events = eventsStatistic.getAllEventStatistic();
        Assert.assertEquals(events.size(), expected.size());
        expected.forEach(this::checkEventStatistic);
    }

    @Test
    public void emptyTest() {
        checkAllEventStatistics(Map.of());
    }

    @Test
    public void testEventsCount() {
        final int EVENTS_NUM = 1500;

        for (int j = 0; j < EVENTS_NUM; j++) {
            eventsStatistic.incEvent("event");
        }
        checkAllEventStatistics(Map.of("event", EVENTS_NUM / 60.0));
    }

    @Test
    public void testExpired() {
        eventsStatistic.incEvent("event");
        clock.addTime(Duration.ofMinutes(60));
        checkAllEventStatistics(Map.of());
    }


    @Test
    public void testExpirationAdding() {
        eventsStatistic.incEvent("event");
        checkEventStatistic("event", 1.0 / 60);
        clock.addTime(Duration.ofMinutes(40));
        eventsStatistic.incEvent("event");
        checkEventStatistic("event", 2.0 / 60);
        clock.addTime(Duration.ofMinutes(40));
        checkEventStatistic("event", 1.0 / 60);
    }

    @Test
    public void testMultipleEvents() {
        eventsStatistic.incEvent("event1");
        eventsStatistic.incEvent("event2");
        eventsStatistic.incEvent("event2");
        clock.addTime(Duration.ofMinutes(40));
        eventsStatistic.incEvent("event2");
        clock.addTime(Duration.ofMinutes(40));
        checkEventStatistic("event1", 0);
        checkEventStatistic("event2", 1.0 / 60);
        checkAllEventStatistics(Map.of("event2", 1.0 / 60));
    }

}