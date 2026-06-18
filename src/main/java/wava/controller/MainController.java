package wava.controller;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javax.swing.SwingUtilities;
import wava.model.jit.JitEventSummary;
import wava.model.metric.MetricSample;
import wava.model.monitor.MonitorState;
import wava.model.process.JavaProcessInfo;
import wava.model.process.TargetProcessStatus;
import wava.model.warmup.WarmupStabilityPoint;
import wava.model.warmup.WarmupSummary;
import wava.service.metric.MonitorService;
import wava.service.process.JavaProcessScanner;
import wava.service.process.ProcessStatusChecker;
import wava.service.warmup.WarmupAnalyzer;
import wava.service.warmup.WarmupStabilityAnalyzer;
import wava.view.WavaFrame;

public class MainController {
    private final WavaFrame frame;
    private final JavaProcessScanner processScanner;
    private final MonitorService monitorService;
    private final ProcessStatusChecker processStatusChecker;
    private final WarmupAnalyzer warmupAnalyzer;
    private final WarmupStabilityAnalyzer warmupStabilityAnalyzer;
    private final GraphController graphController;
    private final JitController jitController;
    private final JfrController jfrController;
    private final ExportController exportController;
    private TargetProcessStatus targetProcessStatus;
    private MonitorState monitorState;
    private JavaProcessInfo selectedProcess;

    public MainController() {
        frame = new WavaFrame();
        processScanner = new JavaProcessScanner();
        monitorService = new MonitorService();
        processStatusChecker = new ProcessStatusChecker();
        warmupAnalyzer = new WarmupAnalyzer();
        warmupStabilityAnalyzer = new WarmupStabilityAnalyzer();
        graphController = new GraphController(
                frame,
                this::refreshMonitoringSummary,
                frame.getLogPanel()::appendInfo);
        jitController = new JitController(frame);
        jfrController = new JfrController();
        exportController = new ExportController();
        targetProcessStatus = TargetProcessStatus.UNKNOWN;
        monitorState = MonitorState.IDLE;
        bindActions();
        updateState(MonitorState.IDLE);
        frame.getLogPanel().appendInfo(jfrController.getJfrStatus().formatLogMessage());
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
        jitController.resetForMonitoring();
        jfrController.startRecording(selectedProcess, frame.getLogPanel()::appendInfo);
        monitorService.start(selectedProcess, this::showMetricSamples);
        frame.getLogPanel().appendInfo("Monitoring started for " + selectedProcess.formatListItem() + ".");
        frame.getLogPanel().appendInfo("Reading JIT log from " + jitController.getLogPath() + ".");
    }

    private void stopMonitoring(ActionEvent event) {
        jfrController.stopRecording(selectedProcess, frame.getLogPanel()::appendInfo);
        monitorService.stop();
        updateState(MonitorState.STOPPED);
        refreshMonitoringSummary();
        frame.getLogPanel().appendInfo("Monitoring stopped.");
    }

    private void resetMonitoring(ActionEvent event) {
        monitorService.reset();
        jfrController.stopRecording(selectedProcess, frame.getLogPanel()::appendInfo);
        jitController.resetAll();
        jfrController.reset();
        targetProcessStatus = TargetProcessStatus.UNKNOWN;
        updateState(MonitorState.IDLE);
        frame.getLogPanel().clear();
        frame.getLogPanel().appendInfo("Monitoring data reset.");
        graphController.clearData();
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
            Path outputPath = exportController.export(
                    samples,
                    jitController.getJitEvents(),
                    jitController.getJitEventFilter());
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
            jitController.applyProcessLogSuggestion(process);
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
            jitController.readNewEvents();
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
        jfrController.stopRecording(selectedProcess, frame.getLogPanel()::appendInfo);
        updateState(MonitorState.STOPPED);
        refreshMonitoringSummary();
        frame.getLogPanel().appendInfo("Target process ended. Monitoring stopped.");
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
        WarmupStabilityPoint stabilityPoint = warmupStabilityAnalyzer.analyze(
                samples,
                jitController.getJitEvents(),
                jitController.getJitEventFilter());
        JitEventSummary jitSummary = jitController.createSummary();
        graphController.showGraphs(
                samples,
                jitController.getJitEvents(),
                jitController.getJitEventFilter(),
                stabilityPoint);
        MetricSample latestSample = samples.get(samples.size() - 1);
        frame.getLiveMetricsPanel().showMonitoringData(
                selectedProcess,
                latestSample,
                summary,
                stabilityPoint);
        frame.getSummaryPanel().showEventSummary(
                selectedProcess,
                jitController.getJitLogStatus(),
                jitController.getJitFilterText(),
                jfrController.getJfrStatus(),
                jfrController.getJfrRecordingStatus(),
                jfrController.getJfrEventSummary(),
                jitSummary);
    }

    private void applyJitSettings(ActionEvent event) {
        jitController.applySettings();
        graphController.clearMarkers();
        refreshMonitoringSummary();
    }
}
