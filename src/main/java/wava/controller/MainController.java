package wava.controller;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import wava.model.JavaProcessInfo;
import wava.model.MetricSample;
import wava.model.MonitorState;
import wava.service.JavaProcessScanner;
import wava.service.MonitorService;
import wava.view.WavaFrame;

public class MainController {
    private final WavaFrame frame;
    private final JavaProcessScanner processScanner;
    private final MonitorService monitorService;
    private MonitorState monitorState;
    private JavaProcessInfo selectedProcess;

    public MainController() {
        frame = new WavaFrame();
        processScanner = new JavaProcessScanner();
        monitorService = new MonitorService();
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
    }

    private void startMonitoring(ActionEvent event) {
        if (selectedProcess == null) {
            frame.getLogPanel().appendInfo("Select a Java process before starting monitoring.");
            frame.getSummaryPanel().showMessage("No process selected.");
            return;
        }
        updateState(MonitorState.RUNNING);
        monitorService.start(selectedProcess, this::showMetricSamples);
        frame.getLogPanel().appendInfo("Monitoring started for " + selectedProcess.formatListItem() + ".");
    }

    private void stopMonitoring(ActionEvent event) {
        monitorService.stop();
        updateState(MonitorState.STOPPED);
        frame.getLogPanel().appendInfo("Monitoring stopped.");
    }

    private void resetMonitoring(ActionEvent event) {
        monitorService.reset();
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
            frame.getCpuGraphPanel().setValues(extractCpuValues(samples));
            frame.getMemoryGraphPanel().setValues(extractMemoryValues(samples));
            frame.getSummaryPanel().showMonitoringSummary(selectedProcess, samples);
        });
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
}
