package wava.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import wava.model.JavaProcessInfo;

public class ProcessPanel extends JPanel {
    private static final int PREFERRED_WIDTH = 260;

    private final JButton refreshButton;
    private final JLabel statusLabel;
    private final DefaultListModel<JavaProcessInfo> processListModel;
    private final JList<JavaProcessInfo> processList;
    private Consumer<JavaProcessInfo> selectionListener;

    public ProcessPanel() {
        super(new BorderLayout(0, 8));
        setPreferredSize(new Dimension(PREFERRED_WIDTH, 0));
        UiStyle.applyPanelStyle(this, "Java Processes");

        refreshButton = new JButton("Refresh Processes");
        statusLabel = new JLabel("No process loaded");
        statusLabel.setForeground(UiStyle.MUTED_TEXT);
        processListModel = new DefaultListModel<>();
        processList = new JList<>(processListModel);
        processList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        processList.setVisibleRowCount(12);
        processList.addListSelectionListener(event -> notifySelectionChanged());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(new JScrollPane(processList), BorderLayout.CENTER);
    }

    public void setRefreshAction(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }

    public void setSelectionAction(Consumer<JavaProcessInfo> listener) {
        selectionListener = listener;
    }

    public JavaProcessInfo getSelectedProcess() {
        return processList.getSelectedValue();
    }

    public void showProcesses(List<JavaProcessInfo> processes) {
        processListModel.clear();
        if (processes.isEmpty()) {
            statusLabel.setText("No Java process found");
            return;
        }
        for (JavaProcessInfo process : processes) {
            processListModel.addElement(process);
        }
        statusLabel.setText(processes.size() + " processes loaded");
    }

    public void showPlaceholder() {
        processListModel.clear();
        statusLabel.setText("No process loaded");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.add(refreshButton, BorderLayout.NORTH);
        panel.add(statusLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void notifySelectionChanged() {
        if (selectionListener != null && !processList.getValueIsAdjusting()) {
            selectionListener.accept(getSelectedProcess());
        }
    }
}
