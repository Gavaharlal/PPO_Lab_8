package clock;

import java.time.Duration;
import java.time.Instant;

public class FakeClock implements Clock {
    private Instant instant;

    public FakeClock(Instant instant) {
        this.instant = instant;
    }

    @Override
    public Instant now() {
        return instant;
    }

    public void addTime(Duration duration) {
        instant = instant.plus(duration);
    }
}
