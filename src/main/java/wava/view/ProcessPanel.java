package wava.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class ProcessPanel extends JPanel {
    private static final int PREFERRED_WIDTH = 300;

    private final JButton refreshButton;
    private final JList<String> processList;

    public ProcessPanel() {
        super(new BorderLayout(0, 8));
        setBorder(new EmptyBorder(8, 8, 8, 8));
        setPreferredSize(new Dimension(PREFERRED_WIDTH, 0));

        refreshButton = new JButton("Refresh Processes");
        processList = new JList<>(new String[] {"No process loaded"});
        processList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(refreshButton, BorderLayout.NORTH);
        add(new JScrollPane(processList), BorderLayout.CENTER);
    }

    public void setRefreshAction(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }
}
