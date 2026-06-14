package wava.service.graph;

import java.util.List;
import wava.model.graph.GraphDisplayRange;
import wava.model.graph.GraphMarker;
import wava.model.graph.GraphTimeWindow;
import wava.model.graph.GraphViewport;
import wava.model.metric.MetricSample;

public class GraphRangeFilterTest {
    public static void main(String[] args) {
        keepOnlySamplesInRecentRange();
        keepSamplesInManualViewportRange();
        createFixedLatestTimeWindow();
        filterMarkersToTimeWindow();
    }

    private static void keepOnlySamplesInRecentRange() {
        GraphRangeFilter filter = new GraphRangeFilter();
        List<MetricSample> samples = List.of(
                sample(0L),
                sample(100_000L),
                sample(140_000L),
                sample(160_000L));

        List<MetricSample> filteredSamples = filter.filterSamples(samples, GraphDisplayRange.LAST_60_SECONDS);

        assertEquals(3, filteredSamples.size(), "sample count");
        assertEquals(100_000L, filteredSamples.get(0).getTimestampMillis(), "first timestamp");
        assertEquals(160_000L, filteredSamples.get(2).getTimestampMillis(), "last timestamp");
    }

    private static void keepSamplesInManualViewportRange() {
        GraphRangeFilter filter = new GraphRangeFilter();
        List<MetricSample> samples = List.of(
                sample(0L),
                sample(30_000L),
                sample(60_000L),
                sample(90_000L),
                sample(120_000L));
        GraphViewport viewport = new GraphViewport(GraphDisplayRange.LAST_60_SECONDS, false, 0);

        List<MetricSample> filteredSamples = filter.filterSamples(samples, viewport);

        assertEquals(3, filteredSamples.size(), "sample count");
        assertEquals(0L, filteredSamples.get(0).getTimestampMillis(), "first timestamp");
        assertEquals(60_000L, filteredSamples.get(2).getTimestampMillis(), "last timestamp");
    }

    private static void createFixedLatestTimeWindow() {
        GraphRangeFilter filter = new GraphRangeFilter();
        List<MetricSample> samples = List.of(sample(0L), sample(160_000L));

        GraphTimeWindow timeWindow = filter.createTimeWindow(samples, GraphViewport.defaultViewport());

        assertEquals(100_000L, timeWindow.getStartTimestampMillis(), "window start");
        assertEquals(160_000L, timeWindow.getEndTimestampMillis(), "window end");
    }

    private static void filterMarkersToTimeWindow() {
        GraphRangeFilter filter = new GraphRangeFilter();
        List<GraphMarker> markers = List.of(
                new GraphMarker(50_000L, "JIT"),
                new GraphMarker(120_000L, "GC"),
                new GraphMarker(180_000L, "JIT"));

        List<GraphMarker> filteredMarkers = filter.filterMarkers(markers, new GraphTimeWindow(100_000L, 160_000L));

        assertEquals(1, filteredMarkers.size(), "marker count");
        assertEquals("GC", filteredMarkers.get(0).getLabel(), "marker label");
    }

    private static MetricSample sample(long timestampMillis) {
        return new MetricSample(timestampMillis, 1.0, 10.0);
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

    private static void assertEquals(String expected, String actual, String label) {
        if (!expected.equals(actual)) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
