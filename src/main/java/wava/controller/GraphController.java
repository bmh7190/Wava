package wava.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import wava.model.graph.GraphDisplayRange;
import wava.model.graph.GraphMarker;
import wava.model.graph.GraphTimeWindow;
import wava.model.graph.GraphViewport;
import wava.model.jit.JitEvent;
import wava.model.metric.MetricSample;
import wava.model.warmup.WarmupStabilityPoint;
import wava.service.graph.GraphRangeFilter;
import wava.service.jit.JitEventFilter;
import wava.view.WavaFrame;

public class GraphController {
    private final WavaFrame frame;
    private final GraphRangeFilter graphRangeFilter;
    private final Runnable refreshAction;
    private final Consumer<String> logAction;
    private GraphViewport graphViewport;

    public GraphController(WavaFrame frame, Runnable refreshAction, Consumer<String> logAction) {
        this.frame = frame;
        this.refreshAction = refreshAction;
        this.logAction = logAction;
        graphRangeFilter = new GraphRangeFilter();
        graphViewport = GraphViewport.defaultViewport();
        bindActions();
        updateGraphRangeLabel();
    }

    public void showGraphs(
            List<MetricSample> samples,
            List<JitEvent> jitEvents,
            JitEventFilter jitEventFilter,
            WarmupStabilityPoint stabilityPoint) {
        List<GraphMarker> markers = createGraphMarkers(samples, jitEvents, jitEventFilter, stabilityPoint);
        GraphTimeWindow timeWindow = graphRangeFilter.createTimeWindow(samples, graphViewport);
        List<MetricSample> visibleSamples = graphRangeFilter.filterSamples(samples, timeWindow);
        List<GraphMarker> visibleMarkers = graphRangeFilter.filterMarkers(markers, timeWindow);
        frame.getCpuGraphPanel().setTimeWindow(timeWindow);
        frame.getCpuGraphPanel().setSamples(visibleSamples, extractCpuValues(visibleSamples));
        frame.getCpuGraphPanel().setMarkers(visibleMarkers);
        frame.getMemoryGraphPanel().setTimeWindow(timeWindow);
        frame.getMemoryGraphPanel().setSamples(visibleSamples, extractMemoryValues(visibleSamples));
        frame.getMemoryGraphPanel().setMarkers(visibleMarkers);
    }

    public void clearData() {
        frame.getCpuGraphPanel().clearData();
        frame.getMemoryGraphPanel().clearData();
    }

    public void clearMarkers() {
        frame.getCpuGraphPanel().setMarkers(List.of());
        frame.getMemoryGraphPanel().setMarkers(List.of());
    }

    private void bindActions() {
        frame.getGraphViewPanel().setWindowAction(this::changeGraphDisplayRange);
        frame.getGraphViewPanel().setFollowLatestAction(this::changeFollowLatest);
        frame.getGraphViewPanel().setTimelineAction(this::changeGraphPosition);
    }

    private void changeGraphDisplayRange(GraphDisplayRange nextRange) {
        graphViewport = graphViewport.withRange(nextRange);
        applyGraphViewport("Graph window set to " + graphViewport.getRange().getLabel() + ".");
    }

    private void changeFollowLatest(boolean followLatest) {
        graphViewport = graphViewport.withFollowLatest(followLatest);
        applyGraphViewport("Graph follow latest " + (followLatest ? "enabled" : "disabled") + ".");
    }

    private void changeGraphPosition(int position) {
        graphViewport = graphViewport.withPosition(position);
        applyGraphViewport("");
    }

    private void applyGraphViewport(String logMessage) {
        frame.getGraphViewPanel().setGraphNavigationState(graphViewport);
        updateGraphRangeLabel();
        refreshAction.run();
        if (!logMessage.isBlank()) {
            logAction.accept(logMessage);
        }
    }

    private void updateGraphRangeLabel() {
        String label = formatGraphRangeLabel();
        frame.getCpuGraphPanel().setDisplayRangeLabel(label);
        frame.getMemoryGraphPanel().setDisplayRangeLabel(label);
    }

    private String formatGraphRangeLabel() {
        if (graphViewport.isFollowLatest()) {
            return graphViewport.getRange().getLabel() + " latest";
        }
        return graphViewport.getRange().getLabel() + " at " + graphViewport.getPosition() / 10 + "%";
    }

    private List<Double> extractCpuValues(List<MetricSample> samples) {
        List<Double> values = new ArrayList<>();
        for (MetricSample sample : samples) {
            values.add(sample.getCpuUsagePercent());
        }
        return values;
    }

    private List<Double> extractMemoryValues(List<MetricSample> samples) {
        List<Double> values = new ArrayList<>();
        for (MetricSample sample : samples) {
            values.add(sample.getHeapUsedMb());
        }
        return values;
    }

    private List<GraphMarker> createGraphMarkers(
            List<MetricSample> samples,
            List<JitEvent> jitEvents,
            JitEventFilter jitEventFilter,
            WarmupStabilityPoint stabilityPoint) {
        List<GraphMarker> markers = new ArrayList<>();
        for (JitEvent event : jitEvents) {
            if (jitEventFilter.matches(event)) {
                markers.add(new GraphMarker(event.getTimestampMillis(), "JIT"));
            }
        }
        for (MetricSample sample : samples) {
            if (sample.hasGcActivity()) {
                markers.add(new GraphMarker(sample.getTimestampMillis(), "GC"));
            }
        }
        if (stabilityPoint.isAvailable()) {
            markers.add(new GraphMarker(stabilityPoint.getTimestampMillis(), "Stable"));
        }
        return markers;
    }
}
