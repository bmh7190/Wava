package wava.view;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import wava.model.JavaProcessInfo;
import wava.model.MonitorState;

public class SummaryPanel extends JPanel {
    private final JLabel stateLabel;
    private final JTextArea summaryArea;

    public SummaryPanel() {
        super(new BorderLayout(0, 6));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        stateLabel = new JLabel();
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setRows(8);

        add(stateLabel, BorderLayout.NORTH);
        add(summaryArea, BorderLayout.CENTER);
        showMessage("No monitoring data.");
    }

    public void showState(MonitorState state) {
        stateLabel.setText("Monitor State: " + state.getLabel());
    }

    public void showMessage(String message) {
        summaryArea.setText(message);
    }

    public void showSelectedProcess(JavaProcessInfo process) {
        if (process == null) {
            summaryArea.setText("No process selected.");
            return;
        }
        summaryArea.setText("Selected Process" + System.lineSeparator()
                + "PID: " + process.getPid() + System.lineSeparator()
                + "Name: " + process.getDisplayName());
    }
}
