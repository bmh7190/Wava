package wava.service.warmup;

import java.util.ArrayList;
import java.util.List;
import wava.model.jit.JitEvent;
import wava.model.metric.MetricSample;
import wava.model.warmup.WarmupStabilityPoint;
import wava.service.jit.JitEventFilter;

public class WarmupStabilityAnalyzerTest {
    public static void main(String[] args) {
        returnUnavailableWhenSamplesAreNotEnough();
        findStablePointWhenCpuRangeIsSmallAndJitEventsDoNotIncrease();
        returnUnavailableWhenCpuRangeIsTooWide();
        respectJitFilterWhenCountingEvents();
    }

    private static void returnUnavailableWhenSamplesAreNotEnough() {
        WarmupStabilityAnalyzer analyzer = new WarmupStabilityAnalyzer();

        WarmupStabilityPoint point = analyzer.analyze(samples(9, 1.0), List.of(), new JitEventFilter(""));

        assertTrue(!point.isAvailable(), "unavailable");
        assertEquals(9, point.getSampleCount(), "sample count");
    }

    private static void findStablePointWhenCpuRangeIsSmallAndJitEventsDoNotIncrease() {
        WarmupStabilityAnalyzer analyzer = new WarmupStabilityAnalyzer();
        List<MetricSample> samples = stableAfterWarmupSamples();
        List<JitEvent> events = List.of(
                event(1500L, "sample.Target::run"),
                event(2500L, "sample.Target::run"));

        WarmupStabilityPoint point = analyzer.analyze(samples, events, new JitEventFilter("Target"));

        assertTrue(point.isAvailable(), "available");
        assertEquals(5, point.getSampleIndex(), "sample index");
        assertEquals(5000L, point.getTimestampMillis(), "timestamp");
        assertEquals(0.4, point.getCpuRangePercent(), "cpu range");
        assertEquals(0, point.getJitEventCount(), "jit count");
    }

    private static void returnUnavailableWhenCpuRangeIsTooWide() {
        WarmupStabilityAnalyzer analyzer = new WarmupStabilityAnalyzer();

        WarmupStabilityPoint point = analyzer.analyze(wideCpuSamples(), List.of(), new JitEventFilter(""));

        assertTrue(!point.isAvailable(), "unavailable");
    }

    private static void respectJitFilterWhenCountingEvents() {
        WarmupStabilityAnalyzer analyzer = new WarmupStabilityAnalyzer();
        List<MetricSample> samples = stableAfterWarmupSamples();
        List<JitEvent> events = List.of(
                event(5200L, "sample.Other::work"),
                event(5300L, "sample.Other::work"),
                event(5400L, "sample.Other::work"));

        WarmupStabilityPoint point = analyzer.analyze(samples, events, new JitEventFilter("Target"));

        assertTrue(point.isAvailable(), "available");
        assertEquals(0, point.getJitEventCount(), "jit count");
    }

    private static List<MetricSample> stableAfterWarmupSamples() {
        double[] cpuValues = {12.0, 10.0, 8.0, 6.0, 4.0, 1.0, 1.2, 1.3, 1.1, 1.4, 1.2, 1.1};
        List<MetricSample> samples = new ArrayList<>();
        for (int index = 0; index < cpuValues.length; index++) {
            samples.add(new MetricSample(index * 1000L, cpuValues[index], 100.0));
        }
        return samples;
    }

    private static List<MetricSample> wideCpuSamples() {
        List<MetricSample> samples = new ArrayList<>();
        for (int index = 0; index < 12; index++) {
            double cpu = index % 2 == 0 ? 1.0 : 8.0;
            samples.add(new MetricSample(index * 1000L, cpu, 100.0));
        }
        return samples;
    }

    private static List<MetricSample> samples(int count, double cpu) {
        List<MetricSample> samples = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            samples.add(new MetricSample(index * 1000L, cpu, 100.0));
        }
        return samples;
    }

    private static JitEvent event(long timestampMillis, String methodName) {
        return new JitEvent(timestampMillis, 1, "3", methodName, methodName);
    }

    private static void assertTrue(boolean condition, String label) {
        if (!condition) {
            throw new AssertionError(label + " expected true");
        }
    }

    private static void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(long expected, long actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }

    private static void assertEquals(double expected, double actual, String label) {
        if (Math.abs(expected - actual) > 0.0001) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
