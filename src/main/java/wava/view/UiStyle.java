package wava.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public final class UiStyle {
    public static final Color BACKGROUND = new Color(241, 245, 249);
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color SURFACE = new Color(248, 250, 252);
    public static final Color BORDER = new Color(203, 213, 225);
    public static final Color TEXT = new Color(15, 23, 42);
    public static final Color MUTED_TEXT = new Color(71, 85, 105);
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color PRIMARY_SOFT = new Color(219, 234, 254);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color FIELD_BACKGROUND = new Color(255, 255, 255);
    public static final Color SCROLLBAR_THUMB = new Color(148, 163, 184);
    public static final Color SCROLLBAR_TRACK = new Color(241, 245, 249);
    public static final Font APP_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font APP_FONT_BOLD = APP_FONT.deriveFont(Font.BOLD);

    private UiStyle() {
    }

    public static void configureDefaults() {
        UIManager.put("control", BACKGROUND);
        UIManager.put("info", SURFACE);
        UIManager.put("nimbusBase", new Color(71, 85, 105));
        UIManager.put("nimbusBlueGrey", BORDER);
        UIManager.put("nimbusFocus", PRIMARY);
        UIManager.put("text", TEXT);
        UIManager.put("defaultFont", APP_FONT);
    }

    public static JLabel titleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(APP_FONT_BOLD.deriveFont(20f));
        return label;
    }

    public static JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED_TEXT);
        label.setFont(APP_FONT);
        return label;
    }

    public static void applyPanelStyle(JComponent component, String title) {
        component.setBackground(PANEL_BACKGROUND);
        component.setBorder(createPanelBorder(title));
        component.setFont(APP_FONT);
    }

    public static Border createPanelBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER),
                title);
        titledBorder.setTitleColor(TEXT);
        titledBorder.setTitleFont(APP_FONT_BOLD);
        return new CompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10));
    }

    public static void applyButtonStyle(JButton button) {
        button.setFont(APP_FONT_BOLD);
        button.setForeground(TEXT);
        button.setBackground(PRIMARY_SOFT);
        button.setMargin(new Insets(4, 12, 4, 12));
        button.setBorder(new EmptyBorder(5, 12, 5, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(Math.max(76, button.getPreferredSize().width), 30));
    }

    public static void applyPrimaryButtonStyle(JButton button) {
        applyButtonStyle(button);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
    }

    public static void setButtonWidth(JButton button, int width) {
        Dimension size = new Dimension(width, 30);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
    }

    public static void applyTextFieldStyle(JTextField field) {
        field.setFont(APP_FONT);
        field.setForeground(TEXT);
        field.setBackground(FIELD_BACKGROUND);
        field.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(4, 6, 4, 6)));
    }

    public static void applyComboBoxStyle(JComboBox<?> comboBox) {
        comboBox.setFont(APP_FONT);
        comboBox.setForeground(TEXT);
        comboBox.setBackground(FIELD_BACKGROUND);
        comboBox.setFocusable(false);
    }

    public static void applyCheckBoxStyle(JCheckBox checkBox) {
        checkBox.setFont(APP_FONT);
        checkBox.setForeground(TEXT);
        checkBox.setOpaque(false);
        checkBox.setFocusable(false);
    }

    public static void applySliderStyle(JSlider slider) {
        slider.setOpaque(false);
        slider.setFocusable(false);
        slider.setPreferredSize(new Dimension(120, 30));
    }

    public static void applyTextAreaStyle(JTextArea textArea) {
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setForeground(TEXT);
        textArea.setBackground(SURFACE);
        textArea.setCaretColor(PRIMARY);
        textArea.setBorder(new EmptyBorder(6, 6, 6, 6));
    }

    public static void applyListStyle(JList<?> list) {
        list.setFont(APP_FONT);
        list.setForeground(TEXT);
        list.setBackground(SURFACE);
        list.setSelectionBackground(PRIMARY_SOFT);
        list.setSelectionForeground(TEXT);
        list.setFixedCellHeight(22);
    }

    public static void applyScrollPaneStyle(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(SURFACE);
        applyScrollBarStyle(scrollPane.getVerticalScrollBar());
        applyScrollBarStyle(scrollPane.getHorizontalScrollBar());
    }

    public static void applyScrollBarStyle(JScrollBar scrollBar) {
        scrollBar.setUI(new WavaScrollBarUI());
        scrollBar.setUnitIncrement(16);
        if (scrollBar.getOrientation() == JScrollBar.VERTICAL) {
            scrollBar.setPreferredSize(new Dimension(10, 0));
        } else {
            scrollBar.setPreferredSize(new Dimension(0, 10));
        }
    }

    private static class WavaScrollBarUI extends BasicScrollBarUI {
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected void paintTrack(Graphics graphics, JComponent component, Rectangle trackBounds) {
            graphics.setColor(SCROLLBAR_TRACK);
            graphics.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics graphics, JComponent component, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setColor(SCROLLBAR_THUMB);
            graphics2D.fillRoundRect(
                    thumbBounds.x + 2,
                    thumbBounds.y + 2,
                    Math.max(4, thumbBounds.width - 4),
                    Math.max(4, thumbBounds.height - 4),
                    8,
                    8);
            graphics2D.dispose();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }
}
