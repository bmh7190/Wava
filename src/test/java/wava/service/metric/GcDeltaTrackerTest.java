package wava.service.metric;

import wava.model.metric.GcDelta;
import wava.model.metric.GcStatsSnapshot;

public class GcDeltaTrackerTest {
    public static void main(String[] args) {
        returnZeroDeltaForFirstSnapshot();
        calculateDeltaFromPreviousSnapshot();
        returnUnavailableAndResetWhenSnapshotIsUnavailable();
    }

    private static void returnZeroDeltaForFirstSnapshot() {
        GcDeltaTracker tracker = new GcDeltaTracker();

        GcDelta delta = tracker.calculateDelta(1L, GcStatsSnapshot.available(5L, 100L));

        assertTrue(delta.isAvailable(), "available");
        assertEquals(0L, delta.getCountDelta(), "count");
        assertEquals(0L, delta.getTimeDeltaMillis(), "time");
    }

    private static void calculateDeltaFromPreviousSnapshot() {
        GcDeltaTracker tracker = new GcDeltaTracker();

        tracker.calculateDelta(1L, GcStatsSnapshot.available(5L, 100L));
        GcDelta delta = tracker.calculateDelta(1L, GcStatsSnapshot.available(8L, 160L));

        assertEquals(3L, delta.getCountDelta(), "count");
        assertEquals(60L, delta.getTimeDeltaMillis(), "time");
    }

    private static void returnUnavailableAndResetWhenSnapshotIsUnavailable() {
        GcDeltaTracker tracker = new GcDeltaTracker();

        tracker.calculateDelta(1L, GcStatsSnapshot.available(5L, 100L));
        GcDelta unavailable = tracker.calculateDelta(1L, GcStatsSnapshot.unavailable());
        GcDelta next = tracker.calculateDelta(1L, GcStatsSnapshot.available(8L, 160L));

        assertTrue(!unavailable.isAvailable(), "unavailable");
        assertEquals(0L, next.getCountDelta(), "reset count");
        assertEquals(0L, next.getTimeDeltaMillis(), "reset time");
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
