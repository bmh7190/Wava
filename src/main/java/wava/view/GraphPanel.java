package wava.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class GraphPanel extends JPanel {
    private static final int PREFERRED_HEIGHT = 220;
    private static final int LEFT_PADDING = 52;
    private static final int RIGHT_PADDING = 18;
    private static final int TOP_PADDING = 28;
    private static final int BOTTOM_PADDING = 32;

    private final String title;
    private final String unit;
    private final List<Double> values;

    public GraphPanel(String title, String unit) {
        this.title = title;
        this.unit = unit;
        values = new ArrayList<>();
        setPreferredSize(new Dimension(480, PREFERRED_HEIGHT));
        setBorder(new EmptyBorder(8, 8, 8, 8));
        setBackground(Color.WHITE);
    }

    public void setValues(List<Double> nextValues) {
        values.clear();
        values.addAll(nextValues);
        repaint();
    }

    public void clearData() {
        values.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawTitle(g2);
        drawPlotArea(g2);
        if (values.isEmpty()) {
            drawEmptyMessage(g2);
        } else {
            drawLine(g2);
        }
        g2.dispose();
    }

    private void drawTitle(Graphics2D g2) {
        g2.setColor(new Color(35, 35, 35));
        g2.drawString(title, LEFT_PADDING, 18);
    }

    private void drawPlotArea(Graphics2D g2) {
        int left = LEFT_PADDING;
        int top = TOP_PADDING;
        int right = getWidth() - RIGHT_PADDING;
        int bottom = getHeight() - BOTTOM_PADDING;

        g2.setColor(new Color(235, 235, 235));
        g2.fillRect(left, top, right - left, bottom - top);
        g2.setColor(new Color(170, 170, 170));
        g2.drawRect(left, top, right - left, bottom - top);
        g2.setColor(new Color(90, 90, 90));
        g2.drawString(unit, 14, top + 12);
    }

    private void drawEmptyMessage(Graphics2D g2) {
        String message = "Waiting for data";
        FontMetrics metrics = g2.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g2.setColor(new Color(105, 105, 105));
        g2.drawString(message, x, y);
    }

    private void drawLine(Graphics2D g2) {
        int left = LEFT_PADDING;
        int top = TOP_PADDING;
        int right = getWidth() - RIGHT_PADDING;
        int bottom = getHeight() - BOTTOM_PADDING;
        double maxValue = Math.max(1.0, values.stream().mapToDouble(Double::doubleValue).max().orElse(1.0));

        g2.setColor(new Color(37, 99, 235));
        for (int index = 1; index < values.size(); index++) {
            int x1 = calculateX(index - 1, left, right);
            int y1 = calculateY(values.get(index - 1), maxValue, top, bottom);
            int x2 = calculateX(index, left, right);
            int y2 = calculateY(values.get(index), maxValue, top, bottom);
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private int calculateX(int index, int left, int right) {
        if (values.size() == 1) {
            return left;
        }
        return left + (right - left) * index / (values.size() - 1);
    }

    private int calculateY(double value, double maxValue, int top, int bottom) {
        double ratio = value / maxValue;
        return bottom - (int) Math.round((bottom - top) * ratio);
    }
}
