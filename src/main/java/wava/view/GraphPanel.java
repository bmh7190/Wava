package wava.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import wava.model.GraphMarker;
import wava.model.GraphScale;
import wava.model.GraphScaleMode;
import wava.model.GraphTimeline;
import wava.model.MetricSample;

public class GraphPanel extends JPanel {
    private static final int PREFERRED_HEIGHT = 220;
    private static final int LEFT_PADDING = 64;
    private static final int RIGHT_PADDING = 24;
    private static final int TOP_PADDING = 44;
    private static final int BOTTOM_PADDING = 34;
    private static final int AXIS_LABEL_GAP = 12;
    private static final int TICK_COUNT = 5;
    private static final int POINT_RADIUS = 4;
    private static final GraphScale CPU_SCALE = GraphScale.fixed(0.0, 100.0);

    private final String unit;
    private final GraphScaleMode scaleMode;
    private final List<Double> values;
    private final List<Long> timestamps;
    private final List<GraphMarker> markers;

    public GraphPanel(String title, String unit, GraphScaleMode scaleMode) {
        this.unit = unit;
        this.scaleMode = scaleMode;
        values = new ArrayList<>();
        timestamps = new ArrayList<>();
        markers = new ArrayList<>();
        setPreferredSize(new Dimension(480, PREFERRED_HEIGHT));
        UiStyle.applyPanelStyle(this, title);
    }

    public void setValues(List<Double> nextValues) {
        values.clear();
        values.addAll(nextValues);
        timestamps.clear();
        repaint();
    }

    public void setSamples(List<MetricSample> samples, List<Double> nextValues) {
        values.clear();
        values.addAll(nextValues);
        timestamps.clear();
        for (MetricSample sample : samples) {
            timestamps.add(sample.getTimestampMillis());
        }
        repaint();
    }

    public void setMarkers(List<GraphMarker> nextMarkers) {
        markers.clear();
        markers.addAll(nextMarkers);
        repaint();
    }

    public void clearData() {
        values.clear();
        timestamps.clear();
        markers.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GraphScale scale = createScale();
        drawPlotArea(g2, scale);
        if (values.isEmpty()) {
            drawEmptyMessage(g2);
        } else {
            drawMarkers(g2);
            drawLine(g2, scale);
            drawLatestValue(g2);
        }
        g2.dispose();
    }

    private void drawPlotArea(Graphics2D g2, GraphScale scale) {
        int left = LEFT_PADDING;
        int top = TOP_PADDING;
        int right = getWidth() - RIGHT_PADDING;
        int bottom = getHeight() - BOTTOM_PADDING;

        g2.setColor(new Color(248, 250, 252));
        g2.fillRect(left, top, right - left, bottom - top);
        g2.setColor(UiStyle.BORDER);
        g2.drawRect(left, top, right - left, bottom - top);
        g2.setColor(UiStyle.MUTED_TEXT);
        drawAxisUnit(g2, left, top);
        drawGridLines(g2, scale, left, top, right, bottom);
    }

    private void drawAxisUnit(Graphics2D g2, int left, int top) {
        FontMetrics metrics = g2.getFontMetrics();
        int x = left - AXIS_LABEL_GAP - metrics.stringWidth(unit);
        g2.drawString(unit, x, top - 8);
    }

    private void drawGridLines(Graphics2D g2, GraphScale scale, int left, int top, int right, int bottom) {
        FontMetrics metrics = g2.getFontMetrics();
        for (int tickIndex = 0; tickIndex < TICK_COUNT; tickIndex++) {
            int y = top + (bottom - top) * tickIndex / (TICK_COUNT - 1);
            double value = scale.getTickValue(TICK_COUNT - 1 - tickIndex, TICK_COUNT);
            g2.setColor(new Color(226, 232, 240));
            g2.drawLine(left, y, right, y);
            g2.setColor(UiStyle.MUTED_TEXT);
            String label = formatValue(value);
            int x = left - AXIS_LABEL_GAP - metrics.stringWidth(label);
            g2.drawString(label, x, y + 4);
        }
    }

    private void drawEmptyMessage(Graphics2D g2) {
        String message = "Waiting for data";
        FontMetrics metrics = g2.getFontMetrics();
        int left = LEFT_PADDING;
        int top = TOP_PADDING;
        int right = getWidth() - RIGHT_PADDING;
        int bottom = getHeight() - BOTTOM_PADDING;
        int x = left + (right - left - metrics.stringWidth(message)) / 2;
        int y = top + (bottom - top) / 2;
        g2.setColor(new Color(105, 105, 105));
        g2.drawString(message, x, y);
    }

    private void drawLine(Graphics2D g2, GraphScale scale) {
        int left = LEFT_PADDING;
        int top = TOP_PADDING;
        int right = getWidth() - RIGHT_PADDING;
        int bottom = getHeight() - BOTTOM_PADDING;

        g2.setColor(UiStyle.PRIMARY);
        g2.setStroke(new BasicStroke(2f));
        if (values.size() == 1) {
            int x = calculateX(0, left, right);
            int y = calculateY(values.get(0), scale, top, bottom);
            g2.fillOval(x - POINT_RADIUS, y - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);
            return;
        }
        for (int index = 1; index < values.size(); index++) {
            int x1 = calculateX(index - 1, left, right);
            int y1 = calculateY(values.get(index - 1), scale, top, bottom);
            int x2 = calculateX(index, left, right);
            int y2 = calculateY(values.get(index), scale, top, bottom);
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawMarkers(Graphics2D g2) {
        if (markers.isEmpty() || timestamps.isEmpty()) {
            return;
        }

        int left = LEFT_PADDING;
        int top = TOP_PADDING;
        int right = getWidth() - RIGHT_PADDING;
        int bottom = getHeight() - BOTTOM_PADDING;
        GraphTimeline timeline = createTimeline();

        g2.setStroke(new BasicStroke(1f));
        for (GraphMarker marker : markers) {
            int x = timeline.calculateX(marker.getTimestampMillis(), left, right);
            g2.setColor(new Color(UiStyle.DANGER.getRed(), UiStyle.DANGER.getGreen(), UiStyle.DANGER.getBlue(), 150));
            g2.drawLine(x, top, x, bottom);
            drawMarkerLabel(g2, marker, x, top);
        }
    }

    private void drawMarkerLabel(Graphics2D g2, GraphMarker marker, int x, int top) {
        String label = marker.getLabel();
        FontMetrics metrics = g2.getFontMetrics();
        int labelX = Math.min(x + 3, getWidth() - RIGHT_PADDING - metrics.stringWidth(label));
        g2.setColor(UiStyle.DANGER);
        g2.drawString(label, Math.max(LEFT_PADDING, labelX), top + 12);
    }

    private void drawLatestValue(Graphics2D g2) {
        double latestValue = values.get(values.size() - 1);
        String label = "Latest: " + formatValue(latestValue) + " " + unit;
        FontMetrics metrics = g2.getFontMetrics();
        int right = getWidth() - RIGHT_PADDING;
        int x = right - metrics.stringWidth(label);
        g2.setColor(UiStyle.TEXT);
        g2.drawString(label, x, TOP_PADDING - 8);
    }

    private int calculateX(int index, int left, int right) {
        if (!timestamps.isEmpty()) {
            return createTimeline().calculateX(timestamps.get(index), left, right);
        }
        if (values.size() == 1) {
            return left;
        }
        return left + (right - left) * index / (values.size() - 1);
    }

    private int calculateY(double value, GraphScale scale, int top, int bottom) {
        double ratio = scale.normalize(value);
        return bottom - (int) Math.round((bottom - top) * ratio);
    }

    private GraphScale createScale() {
        if (scaleMode == GraphScaleMode.FIXED) {
            return CPU_SCALE;
        }
        return GraphScale.auto(values);
    }

    private GraphTimeline createTimeline() {
        return new GraphTimeline(timestamps.get(0), timestamps.get(timestamps.size() - 1));
    }

    private String formatValue(double value) {
        if (Math.abs(value) >= 10.0) {
            return String.format("%.0f", value);
        }
        return String.format("%.2f", value);
    }
}
