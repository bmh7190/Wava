package wava.controller;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import wava.model.graph.GraphDisplayRange;
import wava.model.graph.GraphMarker;
import wava.model.graph.GraphTimeWindow;
import wava.model.graph.GraphViewport;
import wava.model.jfr.JfrAvailabilityStatus;
import wava.model.jfr.JfrEventSummary;
import wava.model.jfr.JfrRecordingStatus;
import wava.model.process.JavaProcessInfo;
import wava.model.jit.JitEvent;
import wava.model.jit.JitEventSummary;
import wava.model.jit.JitLogStatus;
import wava.model.metric.MetricSample;
import wava.model.monitor.MonitorState;
import wava.model.process.TargetProcessStatus;
import wava.model.warmup.WarmupStabilityPoint;
import wava.model.warmup.WarmupSummary;
import wava.service.export.CsvExportService;
import wava.service.graph.GraphRangeFilter;
import wava.service.process.JavaProcessScanner;
import wava.service.jit.JitEventFilter;
import wava.service.jit.JitLogReader;
import wava.service.jit.JitSummaryAnalyzer;
import wava.service.jfr.JfrAvailabilityChecker;
import wava.service.jfr.JfrEventSummaryReader;
import wava.service.jfr.TargetJfrRecorder;
import wava.service.metric.MonitorService;
import wava.service.process.ProcessStatusChecker;
import wava.service.warmup.WarmupAnalyzer;
import wava.service.warmup.WarmupStabilityAnalyzer;
import wava.view.WavaFrame;

public class MainController {
    private static final Path DEFAULT_EXPORT_PATH = Path.of("exports", "wava-monitoring.csv");
    private static final int GRAPH_TIMELINE_STEP = 100;

    private final WavaFrame frame;
    private final JavaProcessScanner processScanner;
    private final MonitorService monitorService;
    private final ProcessStatusChecker processStatusChecker;
    private final JitLogReader jitLogReader;
    private final CsvExportService csvExportService;
    private final JfrAvailabilityChecker jfrAvailabilityChecker;
    private final JfrEventSummaryReader jfrEventSummaryReader;
    private final TargetJfrRecorder targetJfrRecorder;
    private final GraphRangeFilter graphRangeFilter;
    private final WarmupAnalyzer warmupAnalyzer;
    private final WarmupStabilityAnalyzer warmupStabilityAnalyzer;
    private final JitSummaryAnalyzer jitSummaryAnalyzer;
    private final List<JitEvent> jitEvents;
    private JitEventFilter jitEventFilter;
    private JitLogStatus jitLogStatus;
    private String jitFilterText;
    private String lastJitLogStatusKey;
    private JfrAvailabilityStatus jfrStatus;
    private JfrRecordingStatus jfrRecordingStatus;
    private JfrEventSummary jfrEventSummary;
    private GraphViewport graphViewport;
    private TargetProcessStatus targetProcessStatus;
    private MonitorState monitorState;
    private JavaProcessInfo selectedProcess;

    public MainController() {
        frame = new WavaFrame();
        processScanner = new JavaProcessScanner();
        monitorService = new MonitorService();
        processStatusChecker = new ProcessStatusChecker();
        jitLogReader = new JitLogReader();
        csvExportService = new CsvExportService();
        jfrAvailabilityChecker = new JfrAvailabilityChecker();
        jfrEventSummaryReader = new JfrEventSummaryReader();
        targetJfrRecorder = new TargetJfrRecorder();
        graphRangeFilter = new GraphRangeFilter();
        warmupAnalyzer = new WarmupAnalyzer();
        warmupStabilityAnalyzer = new WarmupStabilityAnalyzer();
        jitSummaryAnalyzer = new JitSummaryAnalyzer();
        jitEvents = new ArrayList<>();
        jitEventFilter = new JitEventFilter("");
        jitLogStatus = jitLogReader.inspectStatus();
        jitFilterText = "";
        lastJitLogStatusKey = "";
        jfrStatus = jfrAvailabilityChecker.check();
        jfrRecordingStatus = JfrRecordingStatus.idle();
        jfrEventSummary = JfrEventSummary.empty();
        graphViewport = GraphViewport.defaultViewport();
        targetProcessStatus = TargetProcessStatus.UNKNOWN;
        monitorState = MonitorState.IDLE;
        bindActions();
        updateState(MonitorState.IDLE);
        updateGraphRangeLabel();
        frame.getLogPanel().appendInfo(jfrStatus.formatLogMessage());
    }

    public void start() {
        frame.setVisible(true);
    }

    private void bindActions() {
        frame.getControlPanel().setStartAction(this::startMonitoring);
        frame.getControlPanel().setStopAction(this::stopMonitoring);
        frame.getControlPanel().setResetAction(this::resetMonitoring);
        frame.getControlPanel().setExportAction(this::exportCsv);
        frame.getProcessPanel().setRefreshAction(this::refreshProcesses);
        frame.getProcessPanel().setSelectionAction(this::selectProcess);
        frame.getLogPanel().setApplySettingsAction(this::applyJitSettings);
        frame.getGraphViewPanel().setWindowAction(this::changeGraphDisplayRange);
        frame.getGraphViewPanel().setFollowLatestAction(this::changeFollowLatest);
        frame.getGraphViewPanel().setTimelineAction(this::changeGraphPosition);
        frame.getGraphViewPanel().setPreviousAction(this::showPreviousGraphWindow);
        frame.getGraphViewPanel().setNextAction(this::showNextGraphWindow);
    }

    private void startMonitoring(ActionEvent event) {
        if (selectedProcess == null) {
            frame.getLogPanel().appendInfo("Select a Java process before starting monitoring.");
            frame.getSummaryPanel().showMessage("No process selected.");
            frame.getLiveMetricsPanel().showSelectedProcess(null);
            return;
        }
        targetProcessStatus = processStatusChecker.check(selectedProcess);
        if (targetProcessStatus != TargetProcessStatus.RUNNING) {
            frame.getLogPanel().appendInfo("Selected process is not running.");
            frame.getSummaryPanel().showMessage("Selected process is not running.");
            return;
        }
        updateState(MonitorState.RUNNING);
        jitLogReader.reset();
        jitEvents.clear();
        updateJitLogStatus(true);
        startJfrRecording();
        monitorService.start(selectedProcess, this::showMetricSamples);
        frame.getLogPanel().appendInfo("Monitoring started for " + selectedProcess.formatListItem() + ".");
        frame.getLogPanel().appendInfo("Reading JIT log from " + jitLogReader.getLogPath() + ".");
    }

    private void stopMonitoring(ActionEvent event) {
        stopJfrRecording();
        monitorService.stop();
        updateState(MonitorState.STOPPED);
        refreshMonitoringSummary();
        frame.getLogPanel().appendInfo("Monitoring stopped.");
    }

    private void resetMonitoring(ActionEvent event) {
        monitorService.reset();
        stopJfrRecording();
        jitLogReader.reset();
        jitEvents.clear();
        jfrRecordingStatus = JfrRecordingStatus.idle();
        jfrEventSummary = JfrEventSummary.empty();
        targetProcessStatus = TargetProcessStatus.UNKNOWN;
        updateState(MonitorState.IDLE);
        frame.getLogPanel().clear();
        frame.getLogPanel().appendInfo("Monitoring data reset.");
        frame.getCpuGraphPanel().clearData();
        frame.getMemoryGraphPanel().clearData();
        frame.getSummaryPanel().showSelectedProcess(selectedProcess);
        frame.getLiveMetricsPanel().showSelectedProcess(selectedProcess);
    }

    private void exportCsv(ActionEvent event) {
        List<MetricSample> samples = monitorService.getSamples();
        if (samples.isEmpty()) {
            frame.getLogPanel().appendInfo("No monitoring data to export.");
            frame.getSummaryPanel().showMessage("No monitoring data to export.");
            return;
        }

        try {
            Path outputPath = csvExportService.export(samples, jitEvents, jitEventFilter, DEFAULT_EXPORT_PATH);
            frame.getLogPanel().appendInfo("Exported monitoring data to " + outputPath + ".");
            frame.getSummaryPanel().showMessage("Exported monitoring data to " + outputPath + ".");
        } catch (IOException exception) {
            frame.getLogPanel().appendInfo("Failed to export CSV: " + exception.getMessage());
            frame.getSummaryPanel().showMessage("Failed to export CSV.");
        }
    }

    private void refreshProcesses(ActionEvent event) {
        try {
            List<JavaProcessInfo> processes = processScanner.scan();
            selectedProcess = null;
            frame.getProcessPanel().showProcesses(processes);
            frame.getSummaryPanel().showMessage("No process selected.");
            frame.getLiveMetricsPanel().showSelectedProcess(null);
            if (processes.isEmpty()) {
                frame.getLogPanel().appendInfo("No Java process found.");
            } else {
                frame.getLogPanel().appendInfo("Loaded " + processes.size() + " Java processes.");
            }
        } catch (IOException exception) {
            selectedProcess = null;
            frame.getProcessPanel().showPlaceholder();
            frame.getSummaryPanel().showMessage("Failed to load processes.");
            frame.getLiveMetricsPanel().showSelectedProcess(null);
            frame.getLogPanel().appendInfo("Failed to load Java processes: " + exception.getMessage());
        }
    }

    private void selectProcess(JavaProcessInfo process) {
        selectedProcess = process;
        targetProcessStatus = processStatusChecker.check(process);
        frame.getSummaryPanel().showSelectedProcess(process);
        frame.getLiveMetricsPanel().showSelectedProcess(process);
        if (process != null) {
            frame.getLogPanel().appendInfo("Selected process " + process.formatListItem() + ".");
        }
    }

    private void updateState(MonitorState nextState) {
        monitorState = nextState;
        frame.getControlPanel().setMonitorState(monitorState);
        frame.getSummaryPanel().showState(monitorState);
    }

    private void showMetricSamples(List<MetricSample> samples) {
        SwingUtilities.invokeLater(() -> {
            showJitEvents();
            showMonitoringData(samples);
            if (targetProcessStatus == TargetProcessStatus.ENDED) {
                stopEndedTargetProcess();
            }
        });
    }

    private void stopEndedTargetProcess() {
        if (monitorState != MonitorState.RUNNING) {
            return;
        }
        monitorService.stop();
        stopJfrRecording();
        updateState(MonitorState.STOPPED);
        refreshMonitoringSummary();
        frame.getLogPanel().appendInfo("Target process ended. Monitoring stopped.");
    }

    private void startJfrRecording() {
        if (!jfrStatus.isAvailable()) {
            jfrRecordingStatus = JfrRecordingStatus.failed("JFR is not available in the current runtime.");
            frame.getLogPanel().appendInfo(jfrRecordingStatus.formatLogMessage());
            return;
        }
        jfrRecordingStatus = targetJfrRecorder.start(selectedProcess);
        jfrEventSummary = JfrEventSummary.empty();
        frame.getLogPanel().appendInfo(jfrRecordingStatus.formatLogMessage());
    }

    private void stopJfrRecording() {
        if (!jfrRecordingStatus.isRecording()) {
            return;
        }
        jfrRecordingStatus = targetJfrRecorder.stop(selectedProcess, jfrRecordingStatus);
        frame.getLogPanel().appendInfo(jfrRecordingStatus.formatLogMessage());
        if (jfrRecordingStatus.hasOutputPath()) {
            jfrEventSummary = jfrEventSummaryReader.read(jfrRecordingStatus.getOutputPath());
            frame.getLogPanel().appendInfo(jfrEventSummary.formatLogMessage());
        }
    }

    private void refreshMonitoringSummary() {
        List<MetricSample> samples = monitorService.getSamples();
        if (!samples.isEmpty()) {
            showMonitoringData(samples);
        }
    }

    private void showMonitoringData(List<MetricSample> samples) {
        targetProcessStatus = processStatusChecker.check(selectedProcess);
        WarmupSummary summary = warmupAnalyzer.analyze(samples);
        WarmupStabilityPoint stabilityPoint = warmupStabilityAnalyzer.analyze(samples, jitEvents, jitEventFilter);
        JitEventSummary jitSummary = jitSummaryAnalyzer.analyze(jitEvents, jitEventFilter);
        List<GraphMarker> markers = createGraphMarkers(samples, stabilityPoint);
        GraphTimeWindow timeWindow = graphRangeFilter.createTimeWindow(samples, graphViewport);
        List<MetricSample> visibleSamples = graphRangeFilter.filterSamples(samples, timeWindow);
        List<GraphMarker> visibleMarkers = graphRangeFilter.filterMarkers(markers, timeWindow);
        frame.getCpuGraphPanel().setTimeWindow(timeWindow);
        frame.getCpuGraphPanel().setSamples(visibleSamples, extractCpuValues(visibleSamples));
        frame.getCpuGraphPanel().setMarkers(visibleMarkers);
        frame.getMemoryGraphPanel().setTimeWindow(timeWindow);
        frame.getMemoryGraphPanel().setSamples(visibleSamples, extractMemoryValues(visibleSamples));
        frame.getMemoryGraphPanel().setMarkers(visibleMarkers);
        MetricSample latestSample = samples.get(samples.size() - 1);
        frame.getLiveMetricsPanel().showMonitoringData(
                selectedProcess,
                latestSample,
                summary,
                stabilityPoint);
        frame.getSummaryPanel().showEventSummary(
                selectedProcess,
                jitLogStatus,
                jitFilterText,
                jfrStatus,
                jfrRecordingStatus,
                jfrEventSummary,
                jitSummary);
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

    private void showPreviousGraphWindow() {
        graphViewport = graphViewport.shiftPosition(-GRAPH_TIMELINE_STEP);
        applyGraphViewport("");
    }

    private void showNextGraphWindow() {
        graphViewport = graphViewport.shiftPosition(GRAPH_TIMELINE_STEP);
        applyGraphViewport("");
    }

    private void applyGraphViewport(String logMessage) {
        frame.getGraphViewPanel().setGraphNavigationState(graphViewport);
        updateGraphRangeLabel();
        refreshMonitoringSummary();
        if (!logMessage.isBlank()) {
            frame.getLogPanel().appendInfo(logMessage);
        }
    }

    private void updateGraphRangeLabel() {
        frame.getCpuGraphPanel().setDisplayRangeLabel(formatGraphRangeLabel());
        frame.getMemoryGraphPanel().setDisplayRangeLabel(formatGraphRangeLabel());
    }

    private String formatGraphRangeLabel() {
        if (graphViewport.isFollowLatest()) {
            return graphViewport.getRange().getLabel() + " latest";
        }
        return graphViewport.getRange().getLabel() + " at " + graphViewport.getPosition() / 10 + "%";
    }

    private void showJitEvents() {
        updateJitLogStatus(false);
        if (!jitLogStatus.isReadable()) {
            return;
        }
        try {
            List<JitEvent> events = jitLogReader.readNewEvents();
            for (JitEvent event : events) {
                jitEvents.add(event);
                if (jitEventFilter.matches(event)) {
                    frame.getLogPanel().appendJitEvent(event);
                }
            }
        } catch (IOException exception) {
            setJitLogStatus(JitLogStatus.readError(jitLogReader.getLogPath(), exception.getMessage()), true);
            frame.getLogPanel().appendInfo("Failed to read JIT log: " + exception.getMessage());
        }
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

    private List<GraphMarker> createGraphMarkers(List<MetricSample> samples, WarmupStabilityPoint stabilityPoint) {
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

    private void applyJitSettings(ActionEvent event) {
        jitLogReader.setLogPath(frame.getLogPanel().getLogPath());
        jitFilterText = frame.getLogPanel().getFilterText().trim();
        jitEventFilter = new JitEventFilter(jitFilterText);
        jitEvents.clear();
        frame.getCpuGraphPanel().setMarkers(List.of());
        frame.getMemoryGraphPanel().setMarkers(List.of());
        frame.getLogPanel().appendInfo("JIT settings applied. Log: " + jitLogReader.getLogPath());
        updateJitLogStatus(true);
    }

    private void updateJitLogStatus(boolean forceLog) {
        setJitLogStatus(jitLogReader.inspectStatus(), forceLog);
    }

    private void setJitLogStatus(JitLogStatus nextStatus, boolean forceLog) {
        jitLogStatus = nextStatus;
        String nextStatusKey = nextStatus.getStatusKey();
        if (forceLog || !nextStatusKey.equals(lastJitLogStatusKey)) {
            frame.getLogPanel().appendInfo(nextStatus.formatLogMessage());
            lastJitLogStatusKey = nextStatusKey;
        }
    }
}
