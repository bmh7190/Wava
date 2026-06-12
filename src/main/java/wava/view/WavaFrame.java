package wava.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import wava.model.GraphScaleMode;

public class WavaFrame extends JFrame {
    private static final int DEFAULT_WIDTH = 1100;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int LEFT_PANEL_WIDTH = 260;
    private static final int PROCESS_PANEL_HEIGHT = 260;
    private static final int SUMMARY_PANEL_HEIGHT = 180;

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
        cpuGraphPanel = new GraphPanel("CPU Usage", "%", GraphScaleMode.FIXED);
        memoryGraphPanel = new GraphPanel("Heap Memory", "MB", GraphScaleMode.AUTO);
        logPanel = new LogPanel();
        summaryPanel = new SummaryPanel();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(createContentPane());
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLocationRelativeTo(null);
    }

    private JPanel createContentPane() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UiStyle.BACKGROUND);
        panel.setBorder(new EmptyBorder(8, 4, 8, 8));
        panel.add(createHeaderPanel(), BorderLayout.NORTH);
        panel.add(createMainSplitPane(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel titleLabel = UiStyle.titleLabel("Wava");
        JLabel subtitleLabel = UiStyle.mutedLabel("JVM warm-up, JIT events, CPU, and heap monitoring");
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(subtitleLabel, BorderLayout.EAST);
        return panel;
    }

    private JSplitPane createMainSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
        splitPane.setBorder(null);
        splitPane.setDividerSize(8);
        splitPane.setResizeWeight(0.0);
        splitPane.setDividerLocation(LEFT_PANEL_WIDTH);
        return splitPane;
    }

    private JSplitPane createLeftPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, processPanel, logPanel);
        splitPane.setBorder(null);
        splitPane.setDividerSize(8);
        splitPane.setResizeWeight(0.0);
        splitPane.setDividerLocation(PROCESS_PANEL_HEIGHT);
        splitPane.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, 0));
        return splitPane;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(createCenterPanel(), BorderLayout.CENTER);
        summaryPanel.setPreferredSize(new Dimension(0, SUMMARY_PANEL_HEIGHT));
        panel.add(summaryPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        JPanel graphPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        graphPanel.setOpaque(false);
        graphPanel.add(cpuGraphPanel);
        graphPanel.add(memoryGraphPanel);
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(graphPanel, BorderLayout.CENTER);
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
