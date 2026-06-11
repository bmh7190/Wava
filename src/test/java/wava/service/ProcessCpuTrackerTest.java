package wava.service;

import wava.model.ProcessCpuSnapshot;

public class ProcessCpuTrackerTest {
    public static void main(String[] args) {
        firstSnapshotReturnsZero();
        calculateCpuUsageFromDeltas();
        clampUsageToHundredPercent();
        ignoreInvalidTimeDelta();
    }

    private static void firstSnapshotReturnsZero() {
        ProcessCpuTracker tracker = new ProcessCpuTracker(4);

        double usage = tracker.calculateUsage(new ProcessCpuSnapshot(1L, 1000L, 100L));

        assertEquals(0.0, usage, "first usage");
    }

    private static void calculateCpuUsageFromDeltas() {
        ProcessCpuTracker tracker = new ProcessCpuTracker(4);

        tracker.calculateUsage(new ProcessCpuSnapshot(1L, 1000L, 100L));
        double usage = tracker.calculateUsage(new ProcessCpuSnapshot(1L, 2000L, 600L));

        assertEquals(12.5, usage, "calculated usage");
    }

    private static void clampUsageToHundredPercent() {
        ProcessCpuTracker tracker = new ProcessCpuTracker(4);

        tracker.calculateUsage(new ProcessCpuSnapshot(1L, 1000L, 0L));
        double usage = tracker.calculateUsage(new ProcessCpuSnapshot(1L, 2000L, 8000L));

        assertEquals(100.0, usage, "clamped usage");
    }

    private static void ignoreInvalidTimeDelta() {
        ProcessCpuTracker tracker = new ProcessCpuTracker(4);

        tracker.calculateUsage(new ProcessCpuSnapshot(1L, 1000L, 100L));
        double usage = tracker.calculateUsage(new ProcessCpuSnapshot(1L, 1000L, 200L));

        assertEquals(0.0, usage, "invalid delta usage");
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
