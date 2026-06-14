package wava.service.graph;

import java.util.ArrayList;
import java.util.List;
import wava.model.graph.GraphDisplayRange;
import wava.model.graph.GraphMarker;
import wava.model.graph.GraphTimeWindow;
import wava.model.graph.GraphViewport;
import wava.model.metric.MetricSample;

public class GraphRangeFilter {
    public List<MetricSample> filterSamples(List<MetricSample> samples, GraphDisplayRange range) {
        return filterSamples(samples, new GraphViewport(range, true, GraphViewport.MAX_POSITION));
    }

    public List<MetricSample> filterSamples(List<MetricSample> samples, GraphViewport viewport) {
        if (samples.isEmpty()) {
            return new ArrayList<>(samples);
        }

        GraphTimeWindow window = createTimeWindow(samples, viewport);
        return filterSamples(samples, window);
    }

    public List<MetricSample> filterSamples(List<MetricSample> samples, GraphTimeWindow window) {
        List<MetricSample> filteredSamples = new ArrayList<>();
        for (MetricSample sample : samples) {
            if (window.contains(sample.getTimestampMillis())) {
                filteredSamples.add(sample);
            }
        }
        return filteredSamples;
    }

    public List<GraphMarker> filterMarkers(
            List<GraphMarker> markers,
            List<MetricSample> visibleSamples,
            GraphDisplayRange range) {
        return filterMarkers(
                markers,
                visibleSamples,
                new GraphViewport(range, true, GraphViewport.MAX_POSITION));
    }

    public List<GraphMarker> filterMarkers(
            List<GraphMarker> markers,
            List<MetricSample> visibleSamples,
            GraphViewport viewport) {
        if (visibleSamples.isEmpty()) {
            return List.of();
        }
        long startTimestamp = visibleSamples.get(0).getTimestampMillis();
        long endTimestamp = visibleSamples.get(visibleSamples.size() - 1).getTimestampMillis();
        return filterMarkers(markers, new GraphTimeWindow(startTimestamp, endTimestamp));
    }

    public List<GraphMarker> filterMarkers(List<GraphMarker> markers, GraphTimeWindow window) {
        if (markers.isEmpty()) {
            return List.of();
        }
        List<GraphMarker> filteredMarkers = new ArrayList<>();
        for (GraphMarker marker : markers) {
            if (window.contains(marker.getTimestampMillis())) {
                filteredMarkers.add(marker);
            }
        }
        return filteredMarkers;
    }

    public GraphTimeWindow createTimeWindow(List<MetricSample> samples, GraphViewport viewport) {
        if (samples.isEmpty()) {
            long durationMillis = viewport.getRange().getDurationMillis();
            return new GraphTimeWindow(0L, durationMillis);
        }
        long firstTimestamp = samples.get(0).getTimestampMillis();
        long latestTimestamp = samples.get(samples.size() - 1).getTimestampMillis();
        long rangeDuration = viewport.getRange().getDurationMillis();
        if (viewport.isFollowLatest()) {
            return new GraphTimeWindow(latestTimestamp - rangeDuration, latestTimestamp);
        }

        long maxStartTimestamp = latestTimestamp - rangeDuration;
        if (maxStartTimestamp <= firstTimestamp) {
            return new GraphTimeWindow(latestTimestamp - rangeDuration, latestTimestamp);
        }
        long movableDuration = maxStartTimestamp - firstTimestamp;
        long startTimestamp = firstTimestamp
                + Math.round(movableDuration * (viewport.getPosition() / (double) GraphViewport.MAX_POSITION));
        return new GraphTimeWindow(startTimestamp, startTimestamp + rangeDuration);
    }
}
