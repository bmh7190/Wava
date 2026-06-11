package wava.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import wava.model.JavaProcessInfo;

public class ProcessPanel extends JPanel {
    private static final int PREFERRED_WIDTH = 300;

    private final JButton refreshButton;
    private final DefaultListModel<String> processListModel;
    private final JList<String> processList;

    public ProcessPanel() {
        super(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(8, 8, 8, 8));
        setPreferredSize(new Dimension(PREFERRED_WIDTH, 0));

        refreshButton = new JButton("Refresh Processes");
        processListModel = new DefaultListModel<>();
        processList = new JList<>(processListModel);
        processList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showPlaceholder("No process loaded");

        add(refreshButton, BorderLayout.NORTH);
        add(new JScrollPane(processList), BorderLayout.CENTER);
    }

    public void setRefreshAction(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }

    public void showProcesses(List<JavaProcessInfo> processes) {
        processListModel.clear();
        if (processes.isEmpty()) {
            showPlaceholder("No Java process found");
            return;
        }
        for (JavaProcessInfo process : processes) {
            processListModel.addElement(process.formatListItem());
        }
    }

    public void showPlaceholder(String message) {
        processListModel.clear();
        processListModel.addElement(message);
    }
}
