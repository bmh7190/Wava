package wava.service;

import java.util.ArrayList;
import java.util.List;
import wava.model.MetricSample;
import wava.model.WarmupSummary;

public class WarmupAnalyzerTest {
    public static void main(String[] args) {
        returnUnavailableWhenSamplesAreNotEnough();
        compareEarlyAndLateAverages();
        ignoreUnavailableHeapSamples();
    }

    private static void returnUnavailableWhenSamplesAreNotEnough() {
        WarmupAnalyzer analyzer = new WarmupAnalyzer();

        WarmupSummary summary = analyzer.analyze(samples(5));

        assertTrue(!summary.isAvailable(), "summary unavailable");
        assertEquals(5, summary.getSampleCount(), "sample count");
    }

    private static void compareEarlyAndLateAverages() {
        WarmupAnalyzer analyzer = new WarmupAnalyzer();

        WarmupSummary summary = analyzer.analyze(samples(10));

        assertTrue(summary.isAvailable(), "summary available");
        assertEquals(10, summary.getSampleCount(), "sample count");
        assertEquals(1.0, summary.getEarlyAverageCpu(), "early cpu");
        assertEquals(8.0, summary.getLateAverageCpu(), "late cpu");
        assertEquals(101.0, summary.getEarlyAverageHeap(), "early heap");
        assertEquals(108.0, summary.getLateAverageHeap(), "late heap");
    }

    private static void ignoreUnavailableHeapSamples() {
        WarmupAnalyzer analyzer = new WarmupAnalyzer();
        List<MetricSample> samples = samples(10);
        samples.set(0, new MetricSample(0L, 0.0, 100.0, false));
        samples.set(7, new MetricSample(7L, 7.0, 107.0, false));

        WarmupSummary summary = analyzer.analyze(samples);

        assertEquals(101.5, summary.getEarlyAverageHeap(), "early heap");
        assertEquals(108.5, summary.getLateAverageHeap(), "late heap");
    }

    private static List<MetricSample> samples(int count) {
        List<MetricSample> samples = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            samples.add(new MetricSample(index, index, 100.0 + index));
        }
        return samples;
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

    private static void assertEquals(double expected, double actual, String label) {
        if (Double.compare(expected, actual) != 0) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
