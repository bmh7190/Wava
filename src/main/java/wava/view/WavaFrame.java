package wava.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import wava.model.graph.GraphScaleMode;

public class WavaFrame extends JFrame {
    private static final int DEFAULT_WIDTH = 1100;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int LEFT_PANEL_WIDTH = 260;
    private static final int BOTTOM_PANEL_HEIGHT = 180;
    private static final int LIVE_METRICS_PANEL_HEIGHT = 330;

    private final ProcessPanel processPanel;
    private final LiveMetricsPanel liveMetricsPanel;
    private final ControlPanel controlPanel;
    private final GraphViewPanel graphViewPanel;
    private final GraphPanel cpuGraphPanel;
    private final GraphPanel memoryGraphPanel;
    private final LogPanel logPanel;
    private final SummaryPanel summaryPanel;

    public WavaFrame() {
        super("Wava");
        processPanel = new ProcessPanel();
        liveMetricsPanel = new LiveMetricsPanel();
        controlPanel = new ControlPanel();
        graphViewPanel = new GraphViewPanel();
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

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, 0));

        JPanel settingsPanel = logPanel.getSettingsPanel();
        settingsPanel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, BOTTOM_PANEL_HEIGHT));
        liveMetricsPanel.setPreferredSize(new Dimension(LEFT_PANEL_WIDTH, LIVE_METRICS_PANEL_HEIGHT));

        panel.add(processPanel, BorderLayout.CENTER);
        panel.add(createLeftBottomPanel(settingsPanel), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createLeftBottomPanel(JPanel settingsPanel) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(liveMetricsPanel);
        panel.add(Box.createVerticalStrut(12));
        panel.add(settingsPanel);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.add(createCenterPanel(), BorderLayout.CENTER);
        panel.add(createBottomPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        topPanel.setOpaque(false);
        topPanel.add(controlPanel);
        topPanel.add(graphViewPanel);
        JPanel graphPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        graphPanel.setOpaque(false);
        graphPanel.add(cpuGraphPanel);
        graphPanel.add(memoryGraphPanel);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(graphPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, BOTTOM_PANEL_HEIGHT));
        panel.add(logPanel);
        panel.add(summaryPanel);
        return panel;
    }

    public ProcessPanel getProcessPanel() {
        return processPanel;
    }

    public LiveMetricsPanel getLiveMetricsPanel() {
        return liveMetricsPanel;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    public GraphViewPanel getGraphViewPanel() {
        return graphViewPanel;
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
