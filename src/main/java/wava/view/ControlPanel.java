package wava.view;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import wava.model.monitor.MonitorState;

public class ControlPanel extends JPanel {
    private final JButton startButton;
    private final JButton stopButton;
    private final JButton resetButton;
    private final JButton exportButton;
    private final JLabel stateLabel;

    public ControlPanel() {
        super(new FlowLayout(FlowLayout.LEFT, 8, 8));
        UiStyle.applyPanelStyle(this, "Monitoring Control");

        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        resetButton = new JButton("Reset");
        exportButton = new JButton("Export CSV");
        stateLabel = new JLabel();

        add(startButton);
        add(stopButton);
        add(resetButton);
        add(exportButton);
        add(stateLabel);
        setMonitorState(MonitorState.IDLE);
    }

    public void setStartAction(ActionListener listener) {
        startButton.addActionListener(listener);
    }

    public void setStopAction(ActionListener listener) {
        stopButton.addActionListener(listener);
    }

    public void setResetAction(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    public void setExportAction(ActionListener listener) {
        exportButton.addActionListener(listener);
    }

    public void setMonitorState(MonitorState state) {
        stateLabel.setText("State: " + state.getLabel());
        stateLabel.setForeground(UiStyle.MUTED_TEXT);
        startButton.setEnabled(state != MonitorState.RUNNING);
        stopButton.setEnabled(state == MonitorState.RUNNING);
    }
}
