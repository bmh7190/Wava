package wava.controller;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import wava.model.GraphMarker;
import wava.model.JavaProcessInfo;
import wava.model.JitEvent;
import wava.model.MetricSample;
import wava.model.MonitorState;
import wava.model.WarmupSummary;
import wava.service.JavaProcessScanner;
import wava.service.JitEventFilter;
import wava.service.JitLogReader;
import wava.service.MonitorService;
import wava.service.WarmupAnalyzer;
import wava.view.WavaFrame;

public class MainController {
    private final WavaFrame frame;
    private final JavaProcessScanner processScanner;
    private final MonitorService monitorService;
    private final JitLogReader jitLogReader;
    private final WarmupAnalyzer warmupAnalyzer;
    private final List<JitEvent> jitEvents;
    private JitEventFilter jitEventFilter;
    private MonitorState monitorState;
    private JavaProcessInfo selectedProcess;

    public MainController() {
        frame = new WavaFrame();
        processScanner = new JavaProcessScanner();
        monitorService = new MonitorService();
        jitLogReader = new JitLogReader();
        warmupAnalyzer = new WarmupAnalyzer();
        jitEvents = new ArrayList<>();
        jitEventFilter = new JitEventFilter("");
        monitorState = MonitorState.IDLE;
        bindActions();
        updateState(MonitorState.IDLE);
    }

    public void start() {
        frame.setVisible(true);
    }

    private void bindActions() {
        frame.getControlPanel().setStartAction(this::startMonitoring);
        frame.getControlPanel().setStopAction(this::stopMonitoring);
        frame.getControlPanel().setResetAction(this::resetMonitoring);
        frame.getProcessPanel().setRefreshAction(this::refreshProcesses);
        frame.getProcessPanel().setSelectionAction(this::selectProcess);
        frame.getLogPanel().setApplySettingsAction(this::applyJitSettings);
    }

    private void startMonitoring(ActionEvent event) {
        if (selectedProcess == null) {
            frame.getLogPanel().appendInfo("Select a Java process before starting monitoring.");
            frame.getSummaryPanel().showMessage("No process selected.");
            return;
        }
        updateState(MonitorState.RUNNING);
        jitLogReader.reset();
        jitEvents.clear();
        monitorService.start(selectedProcess, this::showMetricSamples);
        frame.getLogPanel().appendInfo("Monitoring started for " + selectedProcess.formatListItem() + ".");
        frame.getLogPanel().appendInfo("Reading JIT log from " + jitLogReader.getLogPath() + ".");
    }

    private void stopMonitoring(ActionEvent event) {
        monitorService.stop();
        updateState(MonitorState.STOPPED);
        frame.getLogPanel().appendInfo("Monitoring stopped.");
    }

    private void resetMonitoring(ActionEvent event) {
        monitorService.reset();
        jitLogReader.reset();
        jitEvents.clear();
        updateState(MonitorState.IDLE);
        frame.getLogPanel().clear();
        frame.getLogPanel().appendInfo("Monitoring data reset.");
        frame.getCpuGraphPanel().clearData();
        frame.getMemoryGraphPanel().clearData();
        frame.getSummaryPanel().showSelectedProcess(selectedProcess);
    }

    private void refreshProcesses(ActionEvent event) {
        try {
            List<JavaProcessInfo> processes = processScanner.scan();
            selectedProcess = null;
            frame.getProcessPanel().showProcesses(processes);
            frame.getSummaryPanel().showMessage("No process selected.");
            if (processes.isEmpty()) {
                frame.getLogPanel().appendInfo("No Java process found.");
            } else {
                frame.getLogPanel().appendInfo("Loaded " + processes.size() + " Java processes.");
            }
        } catch (IOException exception) {
            selectedProcess = null;
            frame.getProcessPanel().showPlaceholder();
            frame.getSummaryPanel().showMessage("Failed to load processes.");
            frame.getLogPanel().appendInfo("Failed to load Java processes: " + exception.getMessage());
        }
    }

    private void selectProcess(JavaProcessInfo process) {
        selectedProcess = process;
        frame.getSummaryPanel().showSelectedProcess(process);
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
            List<GraphMarker> markers = createGraphMarkers();
            frame.getCpuGraphPanel().setSamples(samples, extractCpuValues(samples));
            frame.getCpuGraphPanel().setMarkers(markers);
            frame.getMemoryGraphPanel().setSamples(samples, extractMemoryValues(samples));
            frame.getMemoryGraphPanel().setMarkers(markers);
            WarmupSummary summary = warmupAnalyzer.analyze(samples);
            frame.getSummaryPanel().showMonitoringSummary(selectedProcess, samples, summary);
        });
    }

    private void showJitEvents() {
        try {
            List<JitEvent> events = jitLogReader.readNewEvents();
            for (JitEvent event : events) {
                jitEvents.add(event);
                if (jitEventFilter.matches(event)) {
                    frame.getLogPanel().appendJitEvent(event);
                }
            }
        } catch (IOException exception) {
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

    private List<GraphMarker> createGraphMarkers() {
        List<GraphMarker> markers = new ArrayList<>();
        for (JitEvent event : jitEvents) {
            if (jitEventFilter.matches(event)) {
                markers.add(new GraphMarker(event.getTimestampMillis(), "JIT"));
            }
        }
        return markers;
    }

    private void applyJitSettings(ActionEvent event) {
        jitLogReader.setLogPath(frame.getLogPanel().getLogPath());
        jitEventFilter = new JitEventFilter(frame.getLogPanel().getFilterText());
        jitEvents.clear();
        frame.getCpuGraphPanel().setMarkers(List.of());
        frame.getMemoryGraphPanel().setMarkers(List.of());
        frame.getLogPanel().appendInfo("JIT settings applied. Log: " + jitLogReader.getLogPath());
    }
}
