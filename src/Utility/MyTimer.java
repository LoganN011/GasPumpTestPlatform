package Utility;

import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class MyTimer extends AnimationTimer {
    private long startNanos = -1;
    private final LongProperty seconds = new SimpleLongProperty(0);

    @Override
    public void handle(long now) {
        if (startNanos < 0) startNanos = now;
        long s = (now - startNanos) / 1_000_000_000L;
        if (s != seconds.get()) {
            seconds.set(s);
        }
    }

    public LongProperty timeProperty() {
        return seconds;
    }

    public void reset() {
        startNanos = -1;
        seconds.set(0);
    }
}
