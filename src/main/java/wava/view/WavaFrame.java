package wava.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WavaFrame extends JFrame {
    private static final int DEFAULT_WIDTH = 1100;
    private static final int DEFAULT_HEIGHT = 720;

    private final ProcessPanel processPanel;
    private final ControlPanel controlPanel;
    private final GraphPanel cpuGraphPanel;
    private final GraphPanel memoryGraphPanel;
    private final LogPanel logPanel;
    private final SummaryPanel summaryPanel;

    public WavaFrame() {
        super("Wava");
        processPanel = new ProcessPanel();
        controlPanel = new ControlPanel();
        cpuGraphPanel = new GraphPanel("CPU Usage", "%");
        memoryGraphPanel = new GraphPanel("Heap Memory", "MB");
        logPanel = new LogPanel();
        summaryPanel = new SummaryPanel();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(createContentPane());
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
    }

    private JPanel createContentPane() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(processPanel, BorderLayout.WEST);
        panel.add(createCenterPanel(), BorderLayout.CENTER);
        panel.add(createBottomPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel graphPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        graphPanel.add(cpuGraphPanel);
        graphPanel.add(memoryGraphPanel);
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(graphPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 0));
        panel.add(logPanel);
        panel.add(summaryPanel);
        return panel;
    }

    public ProcessPanel getProcessPanel() {
        return processPanel;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public GraphPanel getCpuGraphPanel() {
        return cpuGraphPanel;
    }

    public GraphPanel getMemoryGraphPanel() {
        return memoryGraphPanel;
    }

    public LogPanel getLogPanel() {
        return logPanel;
    }

    public SummaryPanel getSummaryPanel() {
        return summaryPanel;
    }
}
