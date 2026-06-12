package wava.controller;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import wava.model.GraphMarker;
import wava.model.JavaProcessInfo;
import wava.model.JitEvent;
import wava.model.JitEventSummary;
import wava.model.JitLogStatus;
import wava.model.MetricSample;
import wava.model.MonitorState;
import wava.model.TargetProcessStatus;
import wava.model.WarmupStabilityPoint;
import wava.model.WarmupSummary;
import wava.service.CsvExportService;
import wava.service.JavaProcessScanner;
import wava.service.JitEventFilter;
import wava.service.JitLogReader;
import wava.service.JitSummaryAnalyzer;
import wava.service.MonitorService;
import wava.service.ProcessStatusChecker;
import wava.service.WarmupAnalyzer;
import wava.service.WarmupStabilityAnalyzer;
import wava.view.WavaFrame;

public class MainController {
    private static final Path DEFAULT_EXPORT_PATH = Path.of("exports", "wava-monitoring.csv");

    private final WavaFrame frame;
    private final JavaProcessScanner processScanner;
    private final MonitorService monitorService;
    private final ProcessStatusChecker processStatusChecker;
    private final JitLogReader jitLogReader;
    private final CsvExportService csvExportService;
    private final WarmupAnalyzer warmupAnalyzer;
    private final WarmupStabilityAnalyzer warmupStabilityAnalyzer;
    private final JitSummaryAnalyzer jitSummaryAnalyzer;
    private final List<JitEvent> jitEvents;
    private JitEventFilter jitEventFilter;
    private JitLogStatus jitLogStatus;
    private String jitFilterText;
    private String lastJitLogStatusKey;
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
        warmupAnalyzer = new WarmupAnalyzer();
        warmupStabilityAnalyzer = new WarmupStabilityAnalyzer();
        jitSummaryAnalyzer = new JitSummaryAnalyzer();
        jitEvents = new ArrayList<>();
        jitEventFilter = new JitEventFilter("");
        jitLogStatus = jitLogReader.inspectStatus();
        jitFilterText = "";
        lastJitLogStatusKey = "";
        targetProcessStatus = TargetProcessStatus.UNKNOWN;
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
        frame.getControlPanel().setExportAction(this::exportCsv);
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
        targetProcessStatus = TargetProcessStatus.UNKNOWN;
        updateState(MonitorState.IDLE);
        frame.getLogPanel().clear();
        frame.getLogPanel().appendInfo("Monitoring data reset.");
        frame.getCpuGraphPanel().clearData();
        frame.getMemoryGraphPanel().clearData();
        frame.getSummaryPanel().showSelectedProcess(selectedProcess);
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
        targetProcessStatus = processStatusChecker.check(process);
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
            targetProcessStatus = processStatusChecker.check(selectedProcess);
            WarmupSummary summary = warmupAnalyzer.analyze(samples);
            WarmupStabilityPoint stabilityPoint = warmupStabilityAnalyzer.analyze(samples, jitEvents, jitEventFilter);
            JitEventSummary jitSummary = jitSummaryAnalyzer.analyze(jitEvents, jitEventFilter);
            List<GraphMarker> markers = createGraphMarkers(stabilityPoint);
            frame.getCpuGraphPanel().setSamples(samples, extractCpuValues(samples));
            frame.getCpuGraphPanel().setMarkers(markers);
            frame.getMemoryGraphPanel().setSamples(samples, extractMemoryValues(samples));
            frame.getMemoryGraphPanel().setMarkers(markers);
            frame.getSummaryPanel().showMonitoringSummary(
                    selectedProcess,
                    targetProcessStatus,
                    samples,
                    summary,
                    stabilityPoint,
                    jitLogStatus,
                    jitFilterText,
                    jitSummary);
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
        updateState(MonitorState.STOPPED);
        frame.getLogPanel().appendInfo("Target process ended. Monitoring stopped.");
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

    private List<GraphMarker> createGraphMarkers(WarmupStabilityPoint stabilityPoint) {
        List<GraphMarker> markers = new ArrayList<>();
        for (JitEvent event : jitEvents) {
            if (jitEventFilter.matches(event)) {
                markers.add(new GraphMarker(event.getTimestampMillis(), "JIT"));
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
