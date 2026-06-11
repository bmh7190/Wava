package wava.controller;

import java.awt.event.ActionEvent;
import wava.model.MonitorState;
import wava.view.WavaFrame;

public class MainController {
    private final WavaFrame frame;
    private MonitorState monitorState;

    public MainController() {
        frame = new WavaFrame();
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
    }

    private void startMonitoring(ActionEvent event) {
        updateState(MonitorState.RUNNING);
        frame.getLogPanel().appendInfo("Monitoring started.");
    }

    private void stopMonitoring(ActionEvent event) {
        updateState(MonitorState.STOPPED);
        frame.getLogPanel().appendInfo("Monitoring stopped.");
    }

    private void resetMonitoring(ActionEvent event) {
        updateState(MonitorState.IDLE);
        frame.getLogPanel().clear();
        frame.getLogPanel().appendInfo("Monitoring data reset.");
        frame.getCpuGraphPanel().clearData();
        frame.getMemoryGraphPanel().clearData();
        frame.getSummaryPanel().showMessage("No monitoring data.");
    }

    private void refreshProcesses(ActionEvent event) {
        frame.getLogPanel().appendInfo("Process refresh is not implemented yet.");
    }

    private void updateState(MonitorState nextState) {
        monitorState = nextState;
        frame.getControlPanel().setMonitorState(monitorState);
        frame.getSummaryPanel().showState(monitorState);
    }
}
